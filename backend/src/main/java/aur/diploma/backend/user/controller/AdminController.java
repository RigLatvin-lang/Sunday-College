package aur.diploma.backend.user.controller;

import aur.diploma.backend.user.dto.CreateAdminRequest;
import aur.diploma.backend.user.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Создание администратора")
public class AdminController {

    private final UserUseCase userUseCase;

    @PostMapping("/create")
    @Operation(summary = "Создать администратора", description = "Требуется секретный ключ в заголовке X-Security-Key")
    public ResponseEntity<Void> createAdmin(
            @RequestHeader("X-Security-Key") String securityKey,
            @Valid @RequestBody CreateAdminRequest request) {
        userUseCase.createAdmin(securityKey, request);
        return ResponseEntity.ok().build();
    }
}
