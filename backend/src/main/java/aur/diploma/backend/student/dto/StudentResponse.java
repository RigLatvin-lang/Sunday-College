package aur.diploma.backend.student.dto;

import aur.diploma.backend.student.entity.Student;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        Long userId,
        String login,
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String inn,
        String phone
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getUser() != null ? s.getUser().getId() : null,
                s.getUser() != null ? s.getUser().getUsername() : null,
                s.getLastName(),
                s.getFirstName(),
                s.getMiddleName(),
                s.getBirthDate(),
                s.getInn(),
                s.getPhone()
        );
    }
}

