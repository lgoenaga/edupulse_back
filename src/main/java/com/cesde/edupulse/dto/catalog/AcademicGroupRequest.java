package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AcademicGroupRequest(
        @NotBlank String name,
        @NotNull @Positive Long levelId,
        @NotNull @Positive Long techniqueId) {
}