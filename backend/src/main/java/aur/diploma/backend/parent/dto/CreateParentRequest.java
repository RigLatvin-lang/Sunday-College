package aur.diploma.backend.parent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос от администратора на создание учётной записи родителя.
 *
 * @param login      логин для входа в систему
 * @param password   пароль
 * @param lastName   фамилия родителя
 * @param firstName  имя родителя
 * @param middleName отчество (необязательно)
 * @param phone      контактный телефон
 * @param studentId  ID ученика, за которым закрепляется родитель
 */
public record CreateParentRequest(
        @NotBlank String login,
        @NotBlank String password,
        @NotBlank String lastName,
        @NotBlank String firstName,
        String middleName,
        String phone,
        @NotNull Long studentId
) {}
