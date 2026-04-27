package aur.diploma.backend.schedule.controller;

import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.usecase.LessonUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "Расписание (публичный доступ без авторизации)")
public class ScheduleController {

    private final LessonUseCase lessonUseCase;

    @GetMapping
    @Operation(summary = "Получить расписание", description = "Фильтрация по дате, группе и/или преподавателю. Без параметров — все занятия")
    public ResponseEntity<List<LessonResponse>> getSchedule(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long teacherId) {
        return ResponseEntity.ok(lessonUseCase.getSchedule(date, groupId, teacherId));
    }
}
