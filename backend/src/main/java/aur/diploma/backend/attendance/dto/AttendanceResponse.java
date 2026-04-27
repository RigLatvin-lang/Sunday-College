package aur.diploma.backend.attendance.dto;

import aur.diploma.backend.attendance.entity.Attendance;

public record AttendanceResponse(
        Long id,
        Long studentId,
        String studentName,
        Long lessonId,
        Boolean present
) {
    public static AttendanceResponse from(Attendance a) {
        String name = a.getStudent().getLastName() + " " + a.getStudent().getFirstName();
        return new AttendanceResponse(a.getId(), a.getStudent().getId(), name, a.getLesson().getId(), a.getPresent());
    }
}
