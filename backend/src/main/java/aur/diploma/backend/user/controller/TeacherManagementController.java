package aur.diploma.backend.user.controller;

import aur.diploma.backend.user.dto.CreateTeacherRequest;
import aur.diploma.backend.user.dto.TeacherInfo;
import aur.diploma.backend.user.dto.TeacherResponse;
import aur.diploma.backend.user.dto.UpdateTeacherRequest;
import aur.diploma.backend.user.usecase.UserUseCase;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Управление учителями (только админ)")
public class TeacherManagementController {

    private final UserUseCase userUseCase;

    @PostMapping
    @Operation(summary = "Создать учителя", description = "Возвращает логин и пароль для передачи учителю")
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(userUseCase.createTeacher(request));
    }

    @GetMapping
    @Operation(summary = "Список всех учителей")
    public ResponseEntity<List<TeacherInfo>> getAllTeachers() {
        return ResponseEntity.ok(userUseCase.getAllTeachers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить учителя по ID")
    public ResponseEntity<TeacherInfo> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(userUseCase.getTeacherById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные учителя", description = "Можно обновить ФИО, логин и/или пароль")
    public ResponseEntity<TeacherInfo> updateTeacher(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTeacherRequest request) {
        return ResponseEntity.ok(userUseCase.updateTeacher(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить учителя")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        userUseCase.deleteTeacher(id);
        return ResponseEntity.ok().build();
    }
}
