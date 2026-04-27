package aur.diploma.backend.user.dto;

import aur.diploma.backend.user.entity.User;

public record UserProfileResponse(
        Long id,
        String fullName,
        String login,
        String role
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getLogin(),
                user.getRole().name()
        );
    }
}
