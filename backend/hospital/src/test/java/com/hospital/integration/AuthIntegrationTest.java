package com.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.hospital.dto.request.LoginRequest;
import com.hospital.dto.request.PatientRegistrationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void registerPatientAndLogin_success() {
        String email = "inttest.patient@example.com";
        String password = "password123";

        PatientRegistrationRequest req = new PatientRegistrationRequest();
        req.setFirstName("Test");
        req.setLastName("Patient");
        req.setEmail(email);
        req.setPhoneNumber("1234567890");
        req.setGender("Other");
        req.setDateOfBirth("1990-01-01");
        req.setPassword(password);

        ResponseEntity<Map> regResp = restTemplate.postForEntity(baseUrl() + "/api/patients/register", req, Map.class);
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
        ResponseEntity<Map> detailsResp = restTemplate.exchange(baseUrl() + "/api/patients/details/{email}", HttpMethod.GET, entity, Map.class, email);
        assertThat(detailsResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(detailsResp.getBody()).containsEntry("email", email);
    }
}
