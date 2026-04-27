package aur.diploma.backend.parent.usecase;

import aur.diploma.backend.attendance.dto.StudentProfileResponse;
import aur.diploma.backend.attendance.usecase.AttendanceUseCase;
import aur.diploma.backend.group.entity.StudentGroup;
import aur.diploma.backend.group.repository.StudentGroupRepository;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.repository.LessonRepository;
import aur.diploma.backend.parent.dto.CreateParentRequest;
import aur.diploma.backend.parent.dto.ParentResponse;
import aur.diploma.backend.parent.entity.Parent;
import aur.diploma.backend.parent.repository.ParentRepository;
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
public class ParentUseCase {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceUseCase attendanceUseCase;
    private final PasswordEncoder passwordEncoder;

    // ─── Admin operations ────────────────────────────────────────────────

    /**
     * Создать учётную запись родителя и закрепить за учеником.
     * Вызывается администратором.
     */
    @Transactional
    public ParentResponse createParent(CreateParentRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Логин уже занят");
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ученик не найден: " + request.studentId()));

        // 1. Создаём системного пользователя с ролью PARENT
        User user = User.builder()
                .fullName(request.lastName() + " " + request.firstName())
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.PARENT)
                .build();
        userRepository.save(user);

        // 2. Создаём профиль родителя, привязанный к ученику
        Parent parent = Parent.builder()
                .user(user)
                .student(student)
                .lastName(request.lastName())
                .firstName(request.firstName())
                .middleName(request.middleName())
                .phone(request.phone())
                .build();

        return ParentResponse.from(parentRepository.save(parent));
    }

    /** Список всех родителей (для страницы администратора). */
    @Transactional(readOnly = true)
    public List<ParentResponse> getAllParents() {
        return parentRepository.findAllWithDetails().stream()
                .map(ParentResponse::from)
                .toList();
    }

    /**
     * Удалить родителя вместе с его учётной записью.
     * Вызывается администратором.
     */
    @Transactional
    public void deleteParent(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Родитель не найден"));
        Long userId = parent.getUser().getId();
        parentRepository.delete(parent);
        userRepository.deleteById(userId);
    }

    // ─── Parent-facing operations ─────────────────────────────────────────

    /**
     * Расписание группы ребёнка на указанную дату.
     * Если {@code date} не передан — возвращается расписание на ближайшие 7 дней.
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> getChildSchedule(User user, LocalDate date) {
        Parent parent = getParentByUser(user);
        Long groupId = resolveGroupId(parent.getStudent());

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
     * Статистика посещаемости ребёнка:
     * общее число занятий, посещённых, пропущенных, процент, streak,
     * разбивка по предметам и последние записи.
     */
    @Transactional(readOnly = true)
    public StudentProfileResponse getChildAttendance(User user) {
        Parent parent = getParentByUser(user);
        return attendanceUseCase.getStudentProfile(parent.getStudent().getId());
    }

    // ─── helpers ──────────────────────────────────────────────────────────

    private Parent getParentByUser(User user) {
        return parentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Профиль родителя не найден"));
    }

    /**
     * Определяет ID первой группы, в которой состоит ученик.
     * В типичной школьной системе один ученик = одна группа/класс.
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
