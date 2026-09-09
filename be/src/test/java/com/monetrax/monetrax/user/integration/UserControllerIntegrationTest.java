package com.monetrax.monetrax.user.integration;

import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;
import com.monetrax.monetrax.user.dto.*;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanChanges(){
        userRepository.deleteAllUsersExceptSupperUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
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

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
          baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserInformation> resGet = restTemplate.exchange(
                baseUrl + "/user/me", HttpMethod.GET, entity, UserInformation.class);

        assertEquals(resGet.getBody(), resUserInformation);

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

    @Test
    public void createUserThenUpdateItIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userId = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());
        UserUpdate userUpdate = UserUpdate.builder()
                .userEmail("john.smith@example.com")
                .userName("johnsmith")
                .surname("Smith")
                .build();

        HttpEntity<UserUpdate> entity = new HttpEntity<>(userUpdate, headers);

        ResponseEntity<UserInformation> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update", HttpMethod.PATCH, entity, UserInformation.class);

        UserInformation res2 = resUpdate.getBody();

        assertNotEquals(res2, res.getBody());
        assertEquals("john.smith@example.com", res2.getUserEmail());
        assertEquals("johnsmith", res2.getUserName());
        assertEquals("Smith", res2.getSurname());
    }


    @Test
    public void createUserThenTryToUpdateWithNoParamsIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userId = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());

        UserUpdate userUpdate = UserUpdate.builder()
                .build();

        HttpEntity<UserUpdate> entity = new HttpEntity<>(userUpdate, headers);

        ResponseEntity<ErrorResponse> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update", HttpMethod.PATCH, entity, ErrorResponse.class);

        ErrorResponse res2 = resUpdate.getBody();

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("Nothing to update user with.","insertFieldInRequest");
        ErrorResponse errRes = new ErrorResponse(400, expectedErrors);

        assertEquals(errRes, res2);
    }

    @Test
    public void createUserThenTryToUpdateWithEmailThatAlreadyExistsIT(){
        String baseUrl = "http://localhost:" + port;
        // create first user, user that we will update
        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        UserCreation userCreationCopy = userCreation.toBuilder()
            .userEmail("emailThatExistsInDB@email.com")
                .build();
        // create second user, the user that has different email, we will use this mail to update first user, which should trigger error
        ResponseEntity<UserInformation> res2 = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreationCopy, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userIdTarget = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());


        UserUpdate userUpdate = UserUpdate.builder()
                .userEmail("emailThatExistsInDB@email.com")
                .build();

        HttpEntity<UserUpdate> entity = new HttpEntity<>(userUpdate, headers);

        ResponseEntity<ErrorResponse> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update", HttpMethod.PATCH, entity, ErrorResponse.class);

        ErrorResponse resErr = resUpdate.getBody();

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("Cannot update user with present email as the email already exists.","userEmail");
        ErrorResponse errRes = new ErrorResponse(403, expectedErrors);

        assertEquals(errRes, resErr);
    }

    @Test
    public void createUserThenUpdatePasswordIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userId = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());

        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .oldPassword("TestPass123!")
                .newPassword("TestPass123456!")
                .build();

        HttpEntity<UserUpdatePassword> entity = new HttpEntity<>(userUpdatePassword, headers);

        ResponseEntity<UserSuccessfulPasswordUpdate> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update/password", HttpMethod.PATCH, entity, UserSuccessfulPasswordUpdate.class);

        UserSuccessfulPasswordUpdate res2 = resUpdate.getBody();

        UserSuccessfulPasswordUpdate resEx = new UserSuccessfulPasswordUpdate(true);
        assertEquals(resEx, res2);
    }



    @Test
    public void createUserThenUpdatePasswordOldPasswordsIsWrongIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userId = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());

        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .oldPassword("TestPass123!.")
                .newPassword("TestPass123!")
                .build();

        HttpEntity<UserUpdatePassword> entity = new HttpEntity<>(userUpdatePassword, headers);

        ResponseEntity<ErrorResponse> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update/password", HttpMethod.PATCH, entity, ErrorResponse.class);

        ErrorResponse res2 = resUpdate.getBody();

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("The provided old password is not equal to the one provided in database.","incorrectPassword");
        ErrorResponse errRes = new ErrorResponse(400, expectedErrors);

        assertEquals(errRes, res2);
    }

    @Test
    public void createUserThenUpdatePasswordNewOldPasswordsMatchIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);

        assertNotNull(res.getBody().getUserId());
        UUID userId = res.getBody().getUserId();

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl+"/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+resAuth.getBody().getAuthToken());


        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .oldPassword("TestPass123!")
                .newPassword("TestPass123!")
                .build();

        HttpEntity<UserUpdatePassword> entity = new HttpEntity<>(userUpdatePassword, headers);

        ResponseEntity<ErrorResponse> resUpdate = restTemplate.exchange(
                baseUrl + "/user/update/password", HttpMethod.PATCH, entity, ErrorResponse.class);

        ErrorResponse res2 = resUpdate.getBody();

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("The new password must not be equal to the old one.","incorrectPassword");
        ErrorResponse errRes = new ErrorResponse(400, expectedErrors);

        assertEquals(errRes, res2);
    }
}
