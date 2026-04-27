package aur.diploma.backend.student.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank String lastName,
        @NotBlank String firstName,
        String middleName,
        LocalDate birthDate,
        String inn,
        String phone
) {}
