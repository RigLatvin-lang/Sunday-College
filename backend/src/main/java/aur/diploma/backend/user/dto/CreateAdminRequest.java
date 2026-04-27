package aur.diploma.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAdminRequest(
        @NotBlank String login,
        @NotBlank String password
) {}
