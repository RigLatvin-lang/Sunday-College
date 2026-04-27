package aur.diploma.backend.attendance.dto;

import aur.diploma.backend.lesson.dto.LessonResponse;
import java.util.List;

public record LessonAttendanceSummary(
        LessonResponse lesson,
        long totalStudents,
        long presentCount,
        double attendancePercentage,
        List<AttendanceResponse> records
) {}
