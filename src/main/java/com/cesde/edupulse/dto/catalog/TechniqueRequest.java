package com.cesde.edupulse.dto.catalog;

import jakarta.validation.constraints.NotBlank;

public record TechniqueRequest(
        @NotBlank String code,
        @NotBlank String name) {
}