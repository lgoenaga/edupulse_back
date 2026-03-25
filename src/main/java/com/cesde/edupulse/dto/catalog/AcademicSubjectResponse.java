package com.cesde.edupulse.dto.catalog;

public record AcademicSubjectResponse(
        Long id,
        String code,
        String name,
        Long levelId,
        String levelName) {
}