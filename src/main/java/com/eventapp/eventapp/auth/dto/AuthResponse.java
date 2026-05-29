package com.eventapp.eventapp.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String email,
        String role
) {
}