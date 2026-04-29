package aur.diploma.backend.student.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Запрос администратора на создание ученика.
 * login + password обязательны — сразу создаётся аккаунт для входа.
 */
public record CreateStudentRequest(
        @NotBlank String login,
        @NotBlank String password,
        @NotBlank String lastName,
        @NotBlank String firstName,
        String middleName,
        LocalDate birthDate,
        String inn,
        String phone
) {}

