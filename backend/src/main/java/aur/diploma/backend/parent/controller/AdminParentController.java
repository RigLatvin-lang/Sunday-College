package aur.diploma.backend.parent.controller;

import aur.diploma.backend.parent.dto.CreateParentRequest;
import aur.diploma.backend.parent.dto.ParentResponse;
import aur.diploma.backend.parent.usecase.ParentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Административные операции с родителями.
 * Доступны только пользователям с ролью ADMIN.
 */
@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@Tag(name = "Parents (Admin)", description = "Управление учётными записями родителей")
public class AdminParentController {

    private final ParentUseCase parentUseCase;

    @PostMapping
    @Operation(summary = "Создать родителя", description = "Регистрирует учётную запись и закрепляет за учеником")
    public ResponseEntity<ParentResponse> createParent(@Valid @RequestBody CreateParentRequest request) {
        return ResponseEntity.ok(parentUseCase.createParent(request));
    }

    @GetMapping
    @Operation(summary = "Список всех родителей")
    public ResponseEntity<List<ParentResponse>> getAllParents() {
        return ResponseEntity.ok(parentUseCase.getAllParents());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить родителя", description = "Удаляет профиль родителя и его учётную запись")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentUseCase.deleteParent(id);
        return ResponseEntity.noContent().build();
    }
}
