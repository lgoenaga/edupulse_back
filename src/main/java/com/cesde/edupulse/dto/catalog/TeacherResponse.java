package com.cesde.edupulse.dto.catalog;

public record TeacherResponse(
        Long id,
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String fullName) {
}