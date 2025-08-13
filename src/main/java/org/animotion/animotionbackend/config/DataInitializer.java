package org.animotion.animotionbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.UserRepository;
import org.animotion.animotionbackend.services.seeder.ProjectSeederService;
import org.animotion.animotionbackend.services.seeder.UserSeederService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class is responsible for initializing the database with test data on the first startup.
 * It acts as an orchestrator, calling specialized seeder services in the correct order.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserSeederService userSeederService;
    private final ProjectSeederService projectSeederService;

    @Override
    public void run(String... args) throws Exception {
        // We only seed data if the database is empty to avoid duplicates on restart
        if (userRepository.count() > 0) {
            return;
        }

        List<User> createdUsers = userSeederService.seedUsers();
        projectSeederService.seedProjectData(createdUsers);
    }
}