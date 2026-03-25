package com.cesde.edupulse.dto.catalog;

public record StudentResponse(
        Long id,
        String studentCode,
        String firstName,
        String lastName,
        String email,
        String fullName,
        Long groupId,
        String groupName,
        boolean active) {
}