package aur.diploma.backend.lesson.dto;

import aur.diploma.backend.lesson.entity.Lesson;
import java.time.LocalDateTime;

public record LessonResponse(
        Long id,
        String subjectName,
        LocalDateTime dateTime,
        String groupName,
        Long groupId,
        Long teacherId,
        String teacherName,
        String classroom,
        Integer durationMinutes
) {
    public static LessonResponse from(Lesson l) {
        return new LessonResponse(
                l.getId(),
                l.getSubject().getName(),
                l.getDateTime(),
                l.getGroup().getName(),
                l.getGroup().getId(),
                l.getTeacher() != null ? l.getTeacher().getId() : null,
                l.getTeacher() != null ? l.getTeacher().getFullName() : null,
                l.getClassroom(),
                l.getDurationMinutes()
        );
    }
}
