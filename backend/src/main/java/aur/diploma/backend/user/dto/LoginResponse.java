package aur.diploma.backend.user.dto;

public record LoginResponse(
        String token,
        String role,
        String fullName
) {}
