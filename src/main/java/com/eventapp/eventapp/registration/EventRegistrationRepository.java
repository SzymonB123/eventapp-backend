package com.eventapp.eventapp.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

    Optional<EventRegistration> findByTicketToken(String ticketToken);

    List<EventRegistration> findAllByUserId(Long userId);

    List<EventRegistration> findAllByEventId(Long eventId);

    long countByEventIdAndStatusIn(Long eventId, List<EventRegistrationStatus> statuses);
}