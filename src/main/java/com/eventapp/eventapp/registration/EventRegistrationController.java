package com.eventapp.eventapp.registration;

import com.eventapp.eventapp.registration.dto.CheckInRequest;
import com.eventapp.eventapp.registration.dto.CheckInResponse;
import com.eventapp.eventapp.registration.dto.RegistrationResponse;
import com.eventapp.eventapp.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    @PostMapping("/api/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerForEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User currentUser
    ) {
        return registrationService.registerForEvent(eventId, currentUser);
    }

    @GetMapping("/api/registrations/my")
    public List<RegistrationResponse> getMyRegistrations(
            @AuthenticationPrincipal User currentUser
    ) {
        return registrationService.getMyRegistrations(currentUser);
    }

    @DeleteMapping("/api/registrations/{registrationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal User currentUser
    ) {
        registrationService.cancelRegistration(registrationId, currentUser);
    }

    @GetMapping("/api/events/{eventId}/registrations")
    public List<RegistrationResponse> getEventRegistrations(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User currentUser
    ) {
        return registrationService.getEventRegistrations(eventId, currentUser);
    }

    @GetMapping(
            value = "/api/registrations/{registrationId}/qr",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public byte[] getRegistrationQrCode(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal User currentUser
    ) {
        return registrationService.getRegistrationQrCode(registrationId, currentUser);
    }

    @PostMapping("/api/check-in")
    public CheckInResponse checkIn(
            @Valid @RequestBody CheckInRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return registrationService.checkIn(request.ticketToken(), currentUser);
    }
}