package aur.diploma.backend.attendance.dto;

import aur.diploma.backend.student.dto.StudentResponse;
import java.util.List;

public record StudentProfileResponse(
        StudentResponse student,
        AttendanceStatsResponse stats,
        List<SubjectAttendanceResponse> subjectStats,
        List<AttendanceRecordResponse> recentAttendance
) {}
