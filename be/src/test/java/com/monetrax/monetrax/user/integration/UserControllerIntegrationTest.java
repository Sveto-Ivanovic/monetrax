package com.monetrax.monetrax.user.integration;

import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private ObjectMapper objectMapper;

    private UserCreation userCreation = UserCreation.builder()
            .userEmail("john.doe742@example.com")
            .userName("johndoe742")
            .name("John")
            .surname("Doe")
            .dateOfBirth(LocalDate.of(1995, 7, 14))
            .password("TestPass123!")
            .build();

    private String getBaseURL(){
        return "http://localhost:"+port;
    }

    @Test
    public void fetchUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);
        UserInformation resUserInformation = res.getBody();

        ResponseEntity<UserInformation> resGet = restTemplate.getForEntity(
                baseUrl + "/user/me/" + resUserInformation.getUserId(), UserInformation.class);

        assertEquals(resGet.getBody(), resUserInformation);
    }
}
