package aur.diploma.backend.attendance.dto;

public record AttendanceStatsResponse(
        long totalLessons,
        long presentCount,
        long absentCount,
        double attendancePercentage,
        int currentStreak
) {}
