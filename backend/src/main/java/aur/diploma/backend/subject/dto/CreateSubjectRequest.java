package aur.diploma.backend.subject.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSubjectRequest(
        @NotBlank String name
) {}
