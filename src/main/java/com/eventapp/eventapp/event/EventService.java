package com.eventapp.eventapp.event;

import com.eventapp.eventapp.event.dto.CreateEventRequest;
import com.eventapp.eventapp.event.dto.EventResponse;
import com.eventapp.eventapp.event.dto.UpdateEventRequest;
import com.eventapp.eventapp.exception.ResourceNotFoundException;
import com.eventapp.eventapp.integration.nominatim.GeocodingResult;
import com.eventapp.eventapp.integration.nominatim.GeocodingService;
import com.eventapp.eventapp.user.Role;
import com.eventapp.eventapp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final GeocodingService geocodingService;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    public EventResponse getEventById(Long eventId) {
        Event event = getEventOrThrow(eventId);
        return EventMapper.toResponse(event);
    }

    public EventResponse createEvent(CreateEventRequest request, User currentUser) {
        validateEventDates(request.startTime(), request.endTime());

        GeocodingResult geocodingResult = geocodingService.geocode(request.location())
                .orElse(null);

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .displayLocation(geocodingResult != null ? geocodingResult.displayName() : null)
                .latitude(geocodingResult != null ? geocodingResult.latitude() : null)
                .longitude(geocodingResult != null ? geocodingResult.longitude() : null)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .capacity(request.capacity())
                .organizer(currentUser)
                .build();

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toResponse(savedEvent);
    }

    public EventResponse updateEvent(Long eventId, UpdateEventRequest request, User currentUser) {
        validateEventDates(request.startTime(), request.endTime());

        Event event = getEventOrThrow(eventId);
        checkEventOwnershipOrAdmin(event, currentUser);

        GeocodingResult geocodingResult = geocodingService.geocode(request.location())
                .orElse(null);

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setDisplayLocation(geocodingResult != null ? geocodingResult.displayName() : null);
        event.setLatitude(geocodingResult != null ? geocodingResult.latitude() : null);
        event.setLongitude(geocodingResult != null ? geocodingResult.longitude() : null);
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setCapacity(request.capacity());

        Event updatedEvent = eventRepository.save(event);

        return EventMapper.toResponse(updatedEvent);
    }

    public void deleteEvent(Long eventId, User currentUser) {
        Event event = getEventOrThrow(eventId);
        checkEventOwnershipOrAdmin(event, currentUser);

        eventRepository.delete(event);
    }

    public List<EventResponse> getMyEvents(User currentUser) {
        return eventRepository.findAllByOrganizerId(currentUser.getId())
                .stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    private void checkEventOwnershipOrAdmin(Event event, User currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = event.getOrganizer().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to modify this event");
        }
    }

    private void validateEventDates(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }
    }
}