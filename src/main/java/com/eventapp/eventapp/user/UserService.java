package com.eventapp.eventapp.user;

import com.eventapp.eventapp.exception.ResourceNotFoundException;
import com.eventapp.eventapp.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse promoteToOrganizer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("Admin cannot be promoted to organizer");
        }

        if (user.getRole() == Role.ORGANIZER) {
            throw new IllegalStateException("User is already an organizer");
        }

        user.setRole(Role.ORGANIZER);

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }
}