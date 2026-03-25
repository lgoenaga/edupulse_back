package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AcademicLoadRequest(
        @NotNull @Positive Long teacherId,
        @NotNull @Positive Long subjectId,
        @NotNull @Positive Long groupId,
        @NotNull @Positive Long periodId,
        boolean active) {
}