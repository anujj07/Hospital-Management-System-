package com.hospital.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hospital.repository.UserRepository;
import com.hospital.service.UserService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserService userService, UserRepository userRepository) {
        return args -> {
            try {
                if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                    userService.createUser("admin@example.com", "adminpass123", "ADMIN");
                }

                if (userRepository.findByEmail("patient@example.com").isEmpty()) {
                    userService.createUser("patient@example.com", "patientpass", "PATIENT");
                }
            } catch (Exception e) {
                // Log but do not fail startup on seed issues
                System.err.println("User seeding failed: " + e.getMessage());
            }
        };
    }

}
