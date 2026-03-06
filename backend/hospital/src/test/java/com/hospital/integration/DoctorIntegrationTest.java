package com.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.entity.User;
import com.hospital.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.hospital.dto.request.DoctorRequest;
import java.time.LocalDate;
import com.hospital.dto.request.LoginRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DoctorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void registerDoctorAndFetchDetails_success() {
        // create an admin user so we can register doctors (endpoint is protected)
        String adminEmail = "inttest.admin@example.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder().email(adminEmail).password(passwordEncoder.encode("adminpass123")).role("ADMIN").isActive(true).build();
            userRepository.save(admin);
        }

        // authenticate as admin to get JWT
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("inttest.admin@example.com");
        adminLogin.setPassword("adminpass123");
        ResponseEntity<Map> adminLoginResp = restTemplate.postForEntity(baseUrl() + "/api/login", adminLogin, Map.class);
        assertThat(adminLoginResp.getStatusCode().is2xxSuccessful()).isTrue();
        String adminToken = (String) adminLoginResp.getBody().get("token");

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        HttpEntity<Void> adminEntity = new HttpEntity<>(adminHeaders);
        String email = "inttest.doctor@example.com";
        String password = "docpassword";

        DoctorRequest req = new DoctorRequest();
        req.setFirstName("Doc");
        req.setLastName("Tor");
        req.setEmail(email);
        req.setPhoneNumber("0987654321");
        req.setGender("Male");
        req.setDateOfBirth(LocalDate.parse("1980-01-01"));
        req.setPassword(password);
        req.setSpecialization("General");
        req.setJoiningDate(LocalDate.now());
        req.setCity("MetroCity");
        req.setState("StateLand");
        req.setCountry("CountryLand");
        req.setBloodGroup("O+");

        // perform the registration as admin (endpoint requires ROLE_ADMIN)
        ResponseEntity<Map> regResp = restTemplate.exchange(baseUrl() + "/api/doctors/registerDoctor", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), Map.class);
        assertThat(regResp.getStatusCode().is2xxSuccessful()).isTrue();

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);

        ResponseEntity<Map> loginResp = restTemplate.postForEntity(baseUrl() + "/api/login", login, Map.class);
        assertThat(loginResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(loginResp.getBody()).containsKey("token");

        String token = (String) loginResp.getBody().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> detailsResp = restTemplate.exchange(baseUrl() + "/api/doctors/details/{email}", HttpMethod.GET, entity, Map.class, email);
        assertThat(detailsResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(detailsResp.getBody()).containsEntry("email", email);
    }
}
