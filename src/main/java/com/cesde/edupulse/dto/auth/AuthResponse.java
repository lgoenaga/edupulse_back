package com.cesde.edupulse.dto.auth;

public record AuthResponse(
        String token,
        String role,
        String fullName) {
}