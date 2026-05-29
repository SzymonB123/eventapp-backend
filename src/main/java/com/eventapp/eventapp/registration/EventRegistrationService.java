package com.eventapp.eventapp.registration;

import com.eventapp.eventapp.event.Event;
import com.eventapp.eventapp.event.EventRepository;
import com.eventapp.eventapp.exception.ResourceNotFoundException;
import com.eventapp.eventapp.qrcode.QrCodeService;
import com.eventapp.eventapp.registration.dto.CheckInResponse;
import com.eventapp.eventapp.registration.dto.RegistrationResponse;
import com.eventapp.eventapp.user.Role;
import com.eventapp.eventapp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final QrCodeService qrCodeService;

    public RegistrationResponse registerForEvent(Long eventId, User currentUser) {
        Event event = getEventOrThrow(eventId);

        registrationRepository.findByEventIdAndUserId(eventId, currentUser.getId())
                .ifPresent(existingRegistration -> {
                    if (existingRegistration.getStatus() == EventRegistrationStatus.REGISTERED
                            || existingRegistration.getStatus() == EventRegistrationStatus.CHECKED_IN) {
                        throw new IllegalStateException("You are already registered for this event");
                    }
                });

        validateCapacity(event);

        EventRegistration registration = registrationRepository
                .findByEventIdAndUserId(eventId, currentUser.getId())
                .map(existingRegistration -> {
                    existingRegistration.setStatus(EventRegistrationStatus.REGISTERED);
                    existingRegistration.setCancelledAt(null);
                    existingRegistration.setCheckedInAt(null);

                    if (existingRegistration.getTicketToken() == null) {
                        existingRegistration.setTicketToken(generateTicketToken());
                    }

                    return existingRegistration;
                })
                .orElseGet(() -> EventRegistration.builder()
                        .event(event)
                        .user(currentUser)
                        .status(EventRegistrationStatus.REGISTERED)
                        .ticketToken(generateTicketToken())
                        .build());

        EventRegistration savedRegistration = registrationRepository.save(registration);

        return EventRegistrationMapper.toResponse(savedRegistration);
    }

    public List<RegistrationResponse> getMyRegistrations(User currentUser) {
        return registrationRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(EventRegistrationMapper::toResponse)
                .toList();
    }

    public void cancelRegistration(Long registrationId, User currentUser) {
        EventRegistration registration = getRegistrationOrThrow(registrationId);

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = registration.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to cancel this registration");
        }

        if (registration.getStatus() == EventRegistrationStatus.CANCELLED) {
            throw new IllegalStateException("Registration is already cancelled");
        }

        if (registration.getStatus() == EventRegistrationStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel registration after check-in");
        }

        registration.setStatus(EventRegistrationStatus.CANCELLED);
        registration.setCancelledAt(LocalDateTime.now());

        registrationRepository.save(registration);
    }

    public List<RegistrationResponse> getEventRegistrations(Long eventId, User currentUser) {
        Event event = getEventOrThrow(eventId);
        checkEventOwnershipOrAdmin(event, currentUser);

        return registrationRepository.findAllByEventId(eventId)
                .stream()
                .map(EventRegistrationMapper::toResponse)
                .toList();
    }

    public byte[] getRegistrationQrCode(Long registrationId, User currentUser) {
        EventRegistration registration = getRegistrationOrThrow(registrationId);

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = registration.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to get QR code for this registration");
        }

        if (registration.getStatus() != EventRegistrationStatus.REGISTERED) {
            throw new IllegalStateException("QR code is available only for active registrations");
        }

        if (registration.getTicketToken() == null) {
            registration.setTicketToken(generateTicketToken());
            registrationRepository.save(registration);
        }

        return qrCodeService.generateQrCodePng(registration.getTicketToken(), 300, 300);
    }

    public CheckInResponse checkIn(String ticketToken, User currentUser) {
        EventRegistration registration = registrationRepository.findByTicketToken(ticketToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid ticket token"));

        Event event = registration.getEvent();

        checkEventOwnershipOrAdmin(event, currentUser);

        if (registration.getStatus() == EventRegistrationStatus.CANCELLED) {
            throw new IllegalStateException("Registration is cancelled");
        }

        if (registration.getStatus() == EventRegistrationStatus.CHECKED_IN) {
            throw new IllegalStateException("User is already checked in");
        }

        registration.setStatus(EventRegistrationStatus.CHECKED_IN);
        registration.setCheckedInAt(LocalDateTime.now());

        EventRegistration savedRegistration = registrationRepository.save(registration);

        return new CheckInResponse(
                savedRegistration.getId(),
                savedRegistration.getEvent().getId(),
                savedRegistration.getEvent().getTitle(),
                savedRegistration.getUser().getId(),
                savedRegistration.getUser().getEmail(),
                savedRegistration.getStatus().name(),
                savedRegistration.getCheckedInAt()
        );
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    private EventRegistration getRegistrationOrThrow(Long registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
    }

    private void validateCapacity(Event event) {
        long activeRegistrations = registrationRepository.countByEventIdAndStatusIn(
                event.getId(),
                List.of(
                        EventRegistrationStatus.REGISTERED,
                        EventRegistrationStatus.CHECKED_IN
                )
        );

        if (activeRegistrations >= event.getCapacity()) {
            throw new IllegalStateException("Event is full");
        }
    }

    private void checkEventOwnershipOrAdmin(Event event, User currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = event.getOrganizer().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to manage this event");
        }
    }

    private String generateTicketToken() {
        return UUID.randomUUID().toString();
    }
}