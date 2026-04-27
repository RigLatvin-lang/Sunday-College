package aur.diploma.backend.student.controller;

import aur.diploma.backend.student.dto.CreateStudentRequest;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.usecase.StudentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Управление учениками (только админ)")
public class StudentController {

    private final StudentUseCase studentUseCase;

    @PostMapping
    @Operation(summary = "Создать ученика")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Список всех учеников")
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(studentUseCase.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить ученика по ID")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить ученика")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ученика")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentUseCase.delete(id);
        return ResponseEntity.ok().build();
    }
}
