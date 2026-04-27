package aur.diploma.backend.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkAttendanceRequest(
        @NotEmpty @Valid List<MarkAttendanceRequest> records
) {}
