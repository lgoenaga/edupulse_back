package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TeacherRequest(
        @NotBlank String documentNumber,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email) {
}