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
import com.hospital.dto.response.PatientResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void patientUpdateAndDelete_flow() {
        String email = "inttest.patient2@example.com";
        String password = "password321";

        PatientRegistrationRequest req = new PatientRegistrationRequest();
        req.setFirstName("Alice");
        req.setLastName("Patient");
        req.setEmail(email);
        req.setPhoneNumber("1112223333");
        req.setGender("Female");
        req.setDateOfBirth("1992-02-02");
        req.setPassword(password);

        ResponseEntity<Map> regResp = restTemplate.postForEntity(baseUrl() + "/api/patients/register", req, Map.class);
        assertThat(regResp.getStatusCode().is2xxSuccessful()).isTrue();

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);

        ResponseEntity<Map> loginResp = restTemplate.postForEntity(baseUrl() + "/api/login", login, Map.class);
        assertThat(loginResp.getStatusCode().is2xxSuccessful()).isTrue();
        String token = (String) loginResp.getBody().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Update patient
        PatientResponse update = new PatientResponse();
        update.setFirstName("AliceUpdated");
        update.setLastName("Patient");
        update.setEmail(email);
        update.setPhoneNumber("1112223333");

        HttpEntity<PatientResponse> updateEntity = new HttpEntity<>(update, headers);
        ResponseEntity<Map> updateResp = restTemplate.exchange(baseUrl() + "/api/patients/update/{email}", HttpMethod.PUT, updateEntity, Map.class, email);
        assertThat(updateResp.getStatusCode().is2xxSuccessful()).isTrue();

        // Delete patient
        HttpEntity<Void> delEntity = new HttpEntity<>(headers);
        ResponseEntity<String> delResp = restTemplate.exchange(baseUrl() + "/api/patients/delete/{email}", HttpMethod.DELETE, delEntity, String.class, email);
        assertThat(delResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(delResp.getBody()).containsIgnoringCase("deleted");
    }
}
