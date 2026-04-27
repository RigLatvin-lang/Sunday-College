package aur.diploma.backend.group.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AddStudentsRequest(
        @NotEmpty List<Long> studentIds
) {}
