package com.cesde.edupulse.dto.catalog;

public record AcademicLoadResponse(
        Long id,
        Long teacherId,
        String teacherName,
        Long subjectId,
        String subjectName,
        Long groupId,
        String groupName,
        Long periodId,
        String periodName,
        boolean active) {
}