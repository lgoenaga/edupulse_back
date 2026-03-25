package com.cesde.edupulse.dto.catalog;

public record AcademicGroupResponse(
        Long id,
        String name,
        Long levelId,
        String levelName,
        Long techniqueId,
        String techniqueName) {
}