package aur.diploma.backend.user.dto;

public record TeacherResponse(
        Long id,
        String fullName,
        String login,
        String password
) {}
