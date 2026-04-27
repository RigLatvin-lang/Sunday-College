package aur.diploma.backend.user.dto;

import aur.diploma.backend.user.entity.User;

public record TeacherInfo(
        Long id,
        String fullName,
        String login
) {
    public static TeacherInfo from(User user) {
        return new TeacherInfo(user.getId(), user.getFullName(), user.getLogin());
    }
}
