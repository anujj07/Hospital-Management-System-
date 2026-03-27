package com.hospital.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hospital.entity.Admin;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.repository.AdminRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import com.hospital.service.UserService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(
            UserService userService,
            UserRepository userRepository,
            AdminRepository adminRepository,
            PatientRepository patientRepository) {
        return args -> {
            try {
                if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                    userService.createUser("admin@example.com", "adminpass123", "ADMIN");
                }

                if (userRepository.findByEmail("patient@example.com").isEmpty()) {
                    userService.createUser("patient@example.com", "patientpass", "PATIENT");
                }

                User adminUser = userRepository.findByEmail("admin@example.com").orElse(null);
                if (adminUser != null && adminRepository.findByEmail("admin@example.com") == null) {
                    Admin admin = new Admin();
                    admin.setFirstName("System");
                    admin.setLastName("Admin");
                    admin.setEmail("admin@example.com");
                    admin.setPhoneNumber("9999999999");
                    adminRepository.save(admin);
                }

                User patientUser = userRepository.findByEmail("patient@example.com").orElse(null);
                if (patientUser != null && patientRepository.findByEmail("patient@example.com").isEmpty()) {
                    Patient patient = new Patient();
                    patient.setUserId(patientUser.getId());
                    patient.setFirstName("Demo");
                    patient.setLastName("Patient");
                    patient.setEmail("patient@example.com");
                    patient.setPhoneNumber("8888888888");
                    patient.setGender("Other");
                    patient.setDateOfBirth("1998-01-01");
                    patient.setAddress("Demo Address");
                    patient.setCity("Bengaluru");
                    patient.setState("Karnataka");
                    patient.setCountry("India");
                    patient.setActive(true);
                    patientRepository.save(patient);
                }
            } catch (Exception e) {
                // Log but do not fail startup on seed issues
                System.err.println("User seeding failed: " + e.getMessage());
            }
        };
    }

}
