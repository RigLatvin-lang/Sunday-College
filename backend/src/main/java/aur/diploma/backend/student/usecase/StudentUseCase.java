package aur.diploma.backend.student.usecase;

import aur.diploma.backend.attendance.dto.StudentProfileResponse;
import aur.diploma.backend.attendance.usecase.AttendanceUseCase;
import aur.diploma.backend.group.entity.StudentGroup;
import aur.diploma.backend.group.repository.StudentGroupRepository;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.repository.LessonRepository;
import aur.diploma.backend.student.dto.CreateStudentRequest;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.entity.Student;
import aur.diploma.backend.student.repository.StudentRepository;
import aur.diploma.backend.user.entity.Role;
import aur.diploma.backend.user.entity.User;
import aur.diploma.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentUseCase {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceUseCase attendanceUseCase;
    private final PasswordEncoder passwordEncoder;

    // ─── Admin operations ────────────────────────────────────────────────

    /**
     * Создать ученика: одновременно регистрируется системный аккаунт (роль STUDENT)
     * и профиль ученика, привязанный к нему.
     */
    @Transactional
    public StudentResponse create(CreateStudentRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Логин уже занят: " + request.login());
        }

        // 1. Создаём системного пользователя с ролью STUDENT
        User user = User.builder()
                .fullName(request.lastName() + " " + request.firstName())
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        // 2. Создаём профиль ученика, привязанный к аккаунту
        Student student = Student.builder()
                .user(user)
                .lastName(request.lastName())
                .firstName(request.firstName())
                .middleName(request.middleName())
                .birthDate(request.birthDate())
                .inn(request.inn())
                .phone(request.phone())
                .build();

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        return StudentResponse.from(studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found")));
    }

    @Transactional
    public StudentResponse update(Long id, CreateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // Обновляем профиль
        student.setLastName(request.lastName());
        student.setFirstName(request.firstName());
        student.setMiddleName(request.middleName());
        student.setBirthDate(request.birthDate());
        student.setInn(request.inn());
        student.setPhone(request.phone());

        // Обновляем аккаунт (логин / пароль), если переданы
        if (student.getUser() != null) {
            User user = student.getUser();
            user.setFullName(request.lastName() + " " + request.firstName());

            if (request.login() != null && !request.login().isBlank()
                    && !request.login().equals(user.getUsername())) {
                if (userRepository.existsByLogin(request.login())) {
                    throw new IllegalArgumentException("Логин уже занят: " + request.login());
                }
                user.setLogin(request.login());
            }

            if (request.password() != null && !request.password().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.password()));
            }
            userRepository.save(user);
        }

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Long userId = student.getUser() != null ? student.getUser().getId() : null;

        // Сначала удаляем профиль, потом (при наличии) аккаунт
        studentRepository.delete(student);
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    // ─── Student self-service operations ─────────────────────────────────

    /**
     * Профиль авторизованного ученика.
     */
    @Transactional(readOnly = true)
    public StudentResponse getMyProfile(User user) {
        Student student = getStudentByUser(user);
        return StudentResponse.from(student);
    }

    /**
     * Расписание группы ученика.
     * Если {@code date} не передан — ближайшие 7 дней.
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> getMySchedule(User user, LocalDate date) {
        Student student = getStudentByUser(user);
        Long groupId = resolveGroupId(student);

        LocalDateTime from;
        LocalDateTime to;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.atTime(23, 59, 59);
        } else {
            from = LocalDate.now().atStartOfDay();
            to = from.plusDays(7);
        }

        return lessonRepository.findByGroupIdAndDateTimeBetween(groupId, from, to)
                .stream()
                .map(LessonResponse::from)
                .toList();
    }

    /**
     * Статистика посещаемости авторизованного ученика:
     * общее число занятий, посещённых, пропущенных, процент, streak,
     * разбивка по предметам, последние записи.
     */
    @Transactional(readOnly = true)
    public StudentProfileResponse getMyAttendance(User user) {
        Student student = getStudentByUser(user);
        return attendanceUseCase.getStudentProfile(student.getId());
    }

    // ─── helpers ──────────────────────────────────────────────────────────

    private Student getStudentByUser(User user) {
        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Профиль ученика не найден"));
    }

    /**
     * Определяет ID первой группы, в которой состоит ученик.
     */
    private Long resolveGroupId(Student student) {
        List<StudentGroup> groups = studentGroupRepository.findByStudentId(student.getId());
        if (groups.isEmpty()) {
            throw new IllegalStateException(
                    "Ученик " + student.getId() + " не состоит ни в одной группе");
        }
        return groups.get(0).getId();
    }
}

