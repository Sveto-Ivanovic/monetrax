package com.monetrax.monetrax.user.integration;

import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.repository.UserRepository;
import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    private void cleanChanges(){
        userRepository.deleteAll();
    }

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
    public void createThenFetchUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);
        UserInformation resUserInformation = res.getBody();

        ResponseEntity<UserInformation> resGet = restTemplate.getForEntity(
                baseUrl + "/user/me/" + resUserInformation.getUserId(), UserInformation.class);

        assertEquals(resGet.getBody(), resUserInformation);

    }

    @Test
    public void fetchNonExistentUserIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<ErrorResponse> resGet = restTemplate.getForEntity(
                baseUrl + "/user/me/" + "7f3a9c21-6d84-4b17-a5e2-91c0f8d73b49", ErrorResponse.class);

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("No user with id: "+ "7f3a9c21-6d84-4b17-a5e2-91c0f8d73b49","param:user_id");
        ErrorResponse err = new ErrorResponse(404,expectedErrors);
        assertEquals(resGet.getBody(), err);
    }


    @Test
    public void createUserWithAlreadyPresentEmailInDatabaseIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("Cannot create user as email already exists.","userEmail");
        ErrorResponse err = new ErrorResponse(403, expectedErrors);

        ResponseEntity<ErrorResponse> res2 = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, ErrorResponse.class);

        assertEquals(res2.getBody(), err);

    }

}
