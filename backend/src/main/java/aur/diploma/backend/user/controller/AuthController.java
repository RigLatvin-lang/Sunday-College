package aur.diploma.backend.user.controller;

import aur.diploma.backend.user.dto.LoginRequest;
import aur.diploma.backend.user.dto.LoginResponse;
import aur.diploma.backend.user.dto.UserProfileResponse;
import aur.diploma.backend.user.entity.User;
import aur.diploma.backend.user.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Аутентификация")
public class AuthController {

    private final UserUseCase userUseCase;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Возвращает JWT токен")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userUseCase.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Получить данные текущего пользователя", description = "Требуется JWT токен в заголовке Authorization")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userUseCase.getCurrentUserProfile(user));
    }
}
