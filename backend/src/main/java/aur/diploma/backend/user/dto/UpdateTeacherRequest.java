package aur.diploma.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeacherRequest(
        @NotBlank String fullName,
        String login,
        String password
) {}
