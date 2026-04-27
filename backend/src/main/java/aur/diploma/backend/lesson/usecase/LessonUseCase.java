package aur.diploma.backend.lesson.usecase;

import aur.diploma.backend.group.entity.StudentGroup;
import aur.diploma.backend.group.repository.StudentGroupRepository;
import aur.diploma.backend.lesson.dto.CreateLessonRequest;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.entity.Lesson;
import aur.diploma.backend.lesson.repository.LessonRepository;
import aur.diploma.backend.subject.entity.Subject;
import aur.diploma.backend.subject.repository.SubjectRepository;
import aur.diploma.backend.user.entity.User;
import aur.diploma.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonUseCase {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final StudentGroupRepository groupRepository;
    private final UserRepository userRepository;

    public LessonResponse create(CreateLessonRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        StudentGroup group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User teacher = null;
        if (request.teacherId() != null) {
            teacher = userRepository.findById(request.teacherId())
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        }

        Lesson lesson = Lesson.builder()
                .subject(subject)
                .dateTime(request.dateTime())
                .group(group)
                .teacher(teacher)
                .classroom(request.classroom())
                .durationMinutes(request.durationMinutes() != null ? request.durationMinutes() : 90)
                .build();
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getAll() {
        return lessonRepository.findAllWithDetails().stream().map(LessonResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public LessonResponse getById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
        return LessonResponse.from(lesson);
    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getSchedule(LocalDate date, Long groupId, Long teacherId) {
        LocalDateTime from;
        LocalDateTime to;
        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        } else {
            from = LocalDateTime.now().minusYears(1);
            to = LocalDateTime.now().plusYears(1);
        }

        if (groupId != null && teacherId != null) {
            return lessonRepository.findByGroupIdAndTeacherIdAndDateTimeBetween(groupId, teacherId, from, to)
                    .stream().map(LessonResponse::from).toList();
        }
        if (teacherId != null) {
            return lessonRepository.findByTeacherIdAndDateTimeBetween(teacherId, from, to)
                    .stream().map(LessonResponse::from).toList();
        }
        if (groupId != null) {
            return lessonRepository.findByGroupIdAndDateTimeBetween(groupId, from, to)
                    .stream().map(LessonResponse::from).toList();
        }
        return lessonRepository.findByDateTimeBetween(from, to)
                .stream().map(LessonResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getUpcoming() {
        return lessonRepository.findUpcoming(LocalDateTime.now())
                .stream().map(LessonResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getUpcomingForTeacher(Long teacherId) {
        return lessonRepository.findUpcomingByTeacherId(teacherId, LocalDateTime.now())
                .stream().map(LessonResponse::from).toList();
    }
}
