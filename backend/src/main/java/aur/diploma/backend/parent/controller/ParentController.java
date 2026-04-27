package aur.diploma.backend.parent.controller;

import aur.diploma.backend.attendance.dto.StudentProfileResponse;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.parent.usecase.ParentUseCase;
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
 * Эндпоинты для роли PARENT.
 * Все маршруты защищены — доступны только пользователям с ролью PARENT.
 */
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@Tag(name = "Parent", description = "Функции родителя — просмотр данных ребёнка")
public class ParentController {

    private final ParentUseCase parentUseCase;

    /**
     * Расписание группы ребёнка.
     * Параметр {@code date} — конкретная дата (ISO: 2025-03-10).
     * Если не передан — возвращается расписание на ближайшие 7 дней.
     */
    @GetMapping("/child/schedule")
    @Operation(summary = "Расписание ребёнка")
    public ResponseEntity<List<LessonResponse>> getChildSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(parentUseCase.getChildSchedule(user, date));
    }

    /**
     * Статистика посещаемости ребёнка:
     * общее число занятий, посещённых, пропущенных, процент, streak,
     * разбивка по предметам и последние записи.
     */
    @GetMapping("/child/attendance")
    @Operation(summary = "Посещаемость ребёнка")
    public ResponseEntity<StudentProfileResponse> getChildAttendance(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(parentUseCase.getChildAttendance(user));
    }
}
