package aur.diploma.backend.parent.dto;

import aur.diploma.backend.parent.entity.Parent;
import aur.diploma.backend.student.dto.StudentResponse;

public record ParentResponse(
        Long id,
        Long userId,
        String login,
        String lastName,
        String firstName,
        String middleName,
        String phone,
        StudentResponse child
) {
    public static ParentResponse from(Parent p) {
        return new ParentResponse(
                p.getId(),
                p.getUser().getId(),
                p.getUser().getUsername(),
                p.getLastName(),
                p.getFirstName(),
                p.getMiddleName(),
                p.getPhone(),
                StudentResponse.from(p.getStudent())
        );
    }
}
