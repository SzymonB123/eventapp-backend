package com.eventapp.eventapp.user;

import com.eventapp.eventapp.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PatchMapping("/{userId}/promote-to-organizer")
    public UserResponse promoteToOrganizer(@PathVariable Long userId) {
        return userService.promoteToOrganizer(userId);
    }
}