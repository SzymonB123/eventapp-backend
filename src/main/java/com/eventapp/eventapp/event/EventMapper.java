package com.eventapp.eventapp.event;

import com.eventapp.eventapp.event.dto.EventResponse;

public class EventMapper {

    private EventMapper() {
    }

    public static EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getDisplayLocation(),
                event.getLatitude(),
                event.getLongitude(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCapacity(),
                event.getOrganizer().getId(),
                event.getOrganizer().getEmail(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}