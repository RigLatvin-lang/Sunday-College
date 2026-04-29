package aur.diploma.backend.student.controller;

import aur.diploma.backend.attendance.dto.StudentProfileResponse;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.usecase.StudentUseCase;
import aur.diploma.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Личный кабинет ученика.
 * Все маршруты защищены — доступны только пользователям с ролью STUDENT.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Личный кабинет ученика — расписание и посещаемость")
public class StudentSelfController {

    private final StudentUseCase studentUseCase;

    /**
     * Профиль текущего авторизованного ученика.
     */
    @GetMapping("/me")
    @Operation(summary = "Мой профиль", description = "Возвращает профиль авторизованного ученика")
    public ResponseEntity<StudentResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studentUseCase.getMyProfile(user));
    }

    /**
     * Расписание группы ученика.
     * Параметр {@code date} — конкретная дата (ISO: 2025-03-10).
     * Если не передан — возвращается расписание на ближайшие 7 дней.
     */
    @GetMapping("/schedule")
    @Operation(
            summary = "Моё расписание",
            description = "Расписание группы ученика. Без параметра date — ближайшие 7 дней."
    )
    public ResponseEntity<List<LessonResponse>> getMySchedule(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(studentUseCase.getMySchedule(user, date));
    }

    /**
     * Статистика посещаемости: общее число занятий, посещённых, пропущенных,
     * процент, streak, разбивка по предметам, последние 20 записей.
     */
    @GetMapping("/attendance")
    @Operation(
            summary = "Моя посещаемость",
            description = "Полная статистика посещаемости: общая и по каждому предмету"
    )
    public ResponseEntity<StudentProfileResponse> getMyAttendance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studentUseCase.getMyAttendance(user));
    }
}
