package com.eventapp.eventapp.registration.dto;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long userId,
        String userEmail,
        String status,
        String ticketToken,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt,
        LocalDateTime checkedInAt
) {
}