package com.eventapp.eventapp.registration.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(
        @NotBlank
        String ticketToken
) {
}