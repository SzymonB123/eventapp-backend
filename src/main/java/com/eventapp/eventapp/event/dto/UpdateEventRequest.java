package com.eventapp.eventapp.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateEventRequest(
        @NotBlank
        String title,

        String description,

        @NotBlank
        String location,

        @NotNull
        @Future
        LocalDateTime startTime,

        @NotNull
        @Future
        LocalDateTime endTime,

        @NotNull
        @Min(1)
        Integer capacity
) {
}