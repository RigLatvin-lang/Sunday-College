package aur.diploma.backend.subject.controller;

import aur.diploma.backend.subject.dto.CreateSubjectRequest;
import aur.diploma.backend.subject.dto.SubjectResponse;
import aur.diploma.backend.subject.usecase.SubjectUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Управление предметами (только админ)")
public class SubjectController {

    private final SubjectUseCase subjectUseCase;

    @PostMapping
    @Operation(summary = "Создать предмет")
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody CreateSubjectRequest request) {
        return ResponseEntity.ok(subjectUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Список предметов")
    public ResponseEntity<List<SubjectResponse>> getAll() {
        return ResponseEntity.ok(subjectUseCase.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить предмет по ID")
    public ResponseEntity<SubjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить предмет")
    public ResponseEntity<SubjectResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CreateSubjectRequest request) {
        return ResponseEntity.ok(subjectUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить предмет")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectUseCase.delete(id);
        return ResponseEntity.ok().build();
    }
}
