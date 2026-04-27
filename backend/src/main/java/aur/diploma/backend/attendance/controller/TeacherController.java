package aur.diploma.backend.attendance.controller;

import aur.diploma.backend.attendance.dto.BulkAttendanceRequest;
import aur.diploma.backend.attendance.dto.AttendanceResponse;
import aur.diploma.backend.attendance.dto.LessonAttendanceSummary;
import aur.diploma.backend.attendance.dto.StudentProfileResponse;
import aur.diploma.backend.attendance.usecase.AttendanceUseCase;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.usecase.LessonUseCase;
import aur.diploma.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@Tag(name = "Teacher", description = "Функции преподавателя")
public class TeacherController {

    private final LessonUseCase lessonUseCase;
    private final AttendanceUseCase attendanceUseCase;

    @GetMapping("/lessons")
    @Operation(summary = "Предстоящие занятия текущего преподавателя")
    public ResponseEntity<List<LessonResponse>> getUpcomingLessons(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lessonUseCase.getUpcomingForTeacher(user.getId()));
    }

    @GetMapping("/lessons/{id}/attendance")
    @Operation(summary = "Посещаемость по занятию", description = "Список учеников с отметками и статистика")
    public ResponseEntity<LessonAttendanceSummary> getLessonAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceUseCase.getLessonAttendance(id));
    }

    @PostMapping("/lessons/{id}/attendance")
    @Operation(summary = "Отметить посещаемость", description = "Массовая отметка посещаемости для занятия")
    public ResponseEntity<List<AttendanceResponse>> markAttendance(
            @PathVariable Long id,
            @Valid @RequestBody BulkAttendanceRequest request) {
        return ResponseEntity.ok(attendanceUseCase.markAttendance(id, request));
    }

    @GetMapping("/students/{id}/profile")
    @Operation(summary = "Профиль ученика", description = "Информация об ученике со статистикой посещаемости")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceUseCase.getStudentProfile(id));
    }
}
