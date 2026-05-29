package com.eventapp.eventapp.event.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String location,
        String displayLocation,
        Double latitude,
        Double longitude,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer capacity,
        Long organizerId,
        String organizerEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}