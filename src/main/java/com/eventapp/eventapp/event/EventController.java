package com.eventapp.eventapp.event;

import com.eventapp.eventapp.event.dto.CreateEventRequest;
import com.eventapp.eventapp.event.dto.EventResponse;
import com.eventapp.eventapp.event.dto.UpdateEventRequest;
import com.eventapp.eventapp.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public EventResponse getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/my")
    public List<EventResponse> getMyEvents(@AuthenticationPrincipal User currentUser) {
        return eventService.getMyEvents(currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return eventService.createEvent(request, currentUser);
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return eventService.updateEvent(eventId, request, currentUser);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User currentUser
    ) {
        eventService.deleteEvent(eventId, currentUser);
    }
}