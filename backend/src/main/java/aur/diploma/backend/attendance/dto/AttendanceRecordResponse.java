package aur.diploma.backend.attendance.dto;

import java.time.LocalDateTime;

public record AttendanceRecordResponse(
        String subjectName,
        LocalDateTime dateTime,
        Boolean present
) {}
