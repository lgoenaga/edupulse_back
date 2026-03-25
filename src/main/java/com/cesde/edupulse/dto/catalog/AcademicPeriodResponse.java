package com.cesde.edupulse.dto.catalog;

import java.time.LocalDate;

public record AcademicPeriodResponse(
        Long id,
        Integer year,
        Integer termNumber,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        boolean active
) {
}