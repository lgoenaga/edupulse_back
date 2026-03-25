package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcademicLevelRequest(
        @NotBlank String name,
        @NotNull @Min(1) Integer displayOrder) {
}