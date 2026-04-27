package aur.diploma.backend.attendance.dto;

import jakarta.validation.constraints.NotNull;

public record MarkAttendanceRequest(
        @NotNull Long studentId,
        @NotNull Boolean present
) {}
