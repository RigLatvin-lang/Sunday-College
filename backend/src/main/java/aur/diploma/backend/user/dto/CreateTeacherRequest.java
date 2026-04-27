package aur.diploma.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeacherRequest(
        @NotBlank String fullName,
        @NotBlank String login,
        @NotBlank String password
) {}
