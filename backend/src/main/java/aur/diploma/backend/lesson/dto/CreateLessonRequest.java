package aur.diploma.backend.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateLessonRequest(
        @NotNull Long subjectId,
        @NotNull LocalDateTime dateTime,
        @NotNull Long groupId,
        Long teacherId,
        @NotBlank String classroom,
        Integer durationMinutes
) {}
