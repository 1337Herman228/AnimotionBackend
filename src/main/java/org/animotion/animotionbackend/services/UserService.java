package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * A helper method to get the currently authenticated User entity.
     * @return The User object from the database.
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }
}