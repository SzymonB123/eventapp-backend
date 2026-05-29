package com.eventapp.eventapp.registration.dto;

import java.time.LocalDateTime;

public record CheckInResponse(
        Long registrationId,
        Long eventId,
        String eventTitle,
        Long userId,
        String userEmail,
        String status,
        LocalDateTime checkedInAt
) {
}