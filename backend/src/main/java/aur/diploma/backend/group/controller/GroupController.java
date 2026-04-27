package aur.diploma.backend.group.controller;

import aur.diploma.backend.group.dto.AddStudentsRequest;
import aur.diploma.backend.group.dto.CreateGroupRequest;
import aur.diploma.backend.group.dto.GroupResponse;
import aur.diploma.backend.group.usecase.GroupUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Управление группами (только админ)")
public class GroupController {

    private final GroupUseCase groupUseCase;

    @PostMapping
    @Operation(summary = "Создать группу")
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Список всех групп")
    public ResponseEntity<List<GroupResponse>> getAll() {
        return ResponseEntity.ok(groupUseCase.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить группу по ID")
    public ResponseEntity<GroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить группу")
    public ResponseEntity<GroupResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить группу")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupUseCase.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/students")
    @Operation(summary = "Добавить учеников в группу (массово)")
    public ResponseEntity<GroupResponse> addStudents(@PathVariable Long id,
                                                     @Valid @RequestBody AddStudentsRequest request) {
        return ResponseEntity.ok(groupUseCase.addStudents(id, request));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @Operation(summary = "Убрать ученика из группы")
    public ResponseEntity<GroupResponse> removeStudent(@PathVariable Long id,
                                                       @PathVariable Long studentId) {
        return ResponseEntity.ok(groupUseCase.removeStudent(id, studentId));
    }
}
