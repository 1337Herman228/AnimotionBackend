package org.animotion.animotionbackend.services.seeder;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.entity.AuthProvider;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSeederService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates and saves test users if they don't exist.
     *
     * @return A list of the created users.
     */
    public List<User> seedUsers() {
        User localUser = new User();
        localUser.setName("user");
        localUser.setEmail("user@user.com");
        localUser.setPassword(passwordEncoder.encode("useruser"));
        localUser.setProvider(AuthProvider.LOCAL);
        localUser.setImage("https://i.pravatar.cc/150?u=local@user.com");

        User googleUser = new User();
        googleUser.setName("Google User");
        googleUser.setEmail("google@user.com");
        googleUser.setProvider(AuthProvider.GOOGLE);
        googleUser.setProviderId("109876543210987654321");
        googleUser.setImage("https://i.pravatar.cc/150?u=google@user.com");

        return userRepository.saveAll(Arrays.asList(
                localUser,
                googleUser
        ));
    }
}
