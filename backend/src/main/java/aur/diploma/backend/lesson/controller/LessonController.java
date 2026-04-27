package aur.diploma.backend.lesson.controller;

import aur.diploma.backend.lesson.dto.CreateLessonRequest;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.usecase.LessonUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Управление занятиями (только админ)")
public class LessonController {

    private final LessonUseCase lessonUseCase;

    @PostMapping
    @Operation(summary = "Создать занятие")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody CreateLessonRequest request) {
        return ResponseEntity.ok(lessonUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Список всех занятий")
    public ResponseEntity<List<LessonResponse>> getAll() {
        return ResponseEntity.ok(lessonUseCase.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить занятие по ID")
    public ResponseEntity<LessonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonUseCase.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить занятие")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonUseCase.delete(id);
        return ResponseEntity.ok().build();
    }
}
