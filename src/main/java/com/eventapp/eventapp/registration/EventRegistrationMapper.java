package com.eventapp.eventapp.registration;

import com.eventapp.eventapp.registration.dto.RegistrationResponse;

public class EventRegistrationMapper {

    private EventRegistrationMapper() {
    }

    public static RegistrationResponse toResponse(EventRegistration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getEvent().getTitle(),
                registration.getUser().getId(),
                registration.getUser().getEmail(),
                registration.getStatus().name(),
                registration.getTicketToken(),
                registration.getCreatedAt(),
                registration.getCancelledAt(),
                registration.getCheckedInAt()
        );
    }
}