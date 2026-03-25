package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AcademicPeriodRequest(
        @NotNull Integer year,
        @NotNull @Min(1) @Max(2) Integer termNumber,
        @NotBlank String name,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        boolean active
) {
}