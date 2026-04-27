package aur.diploma.backend.attendance.dto;

public record SubjectAttendanceResponse(
        Long subjectId,
        String subjectName,
        long totalLessons,
        long presentCount,
        double attendancePercentage
) {}
