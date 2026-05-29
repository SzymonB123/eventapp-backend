package com.eventapp.eventapp.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestSecurityController {

    @GetMapping("/attendee/test")
    public String attendeeTest() {
        return "Hello attendee";
    }

    @GetMapping("/organizer/test")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String organizerTest() {
        return "Hello organizer";
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminTest() {
        return "Hello admin";
    }
}