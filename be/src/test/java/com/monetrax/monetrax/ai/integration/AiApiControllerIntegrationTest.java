package com.monetrax.monetrax.ai.integration;

import com.monetrax.monetrax.ai.dto.ResponseApiKeyStatusMsg;
import com.monetrax.monetrax.ai.entity.ApiKeysEntity;
import com.monetrax.monetrax.ai.entity.KeyType;
import com.monetrax.monetrax.ai.dto.RequestApiKey;
import com.monetrax.monetrax.ai.repository.ApiKeysRepository;
import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.repository.RefreshTokenRepository;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AiApiControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ApiKeysRepository apiKeysRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String authToken;
    private UUID userId;

    @BeforeAll
    void authenticate() {
        String baseUrl = "http://localhost:" + port;

        UserCreation userCreation = UserCreation.builder()
                .userEmail("jane.roe518@example.com")
                .userName("janeroe518")
                .name("Jane")
                .surname("Roe")
                .dateOfBirth(LocalDate.of(1993, 3, 22))
                .password("TestPass123!")
                .build();

        ResponseEntity<UserInformation> res = restTemplate.postForEntity(
                baseUrl + "/user/create", userCreation, UserInformation.class);
        UserInformation resUserInformation = res.getBody();

        AuthRequest authRequest = AuthRequest.builder()
                .email("jane.roe518@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl + "/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        this.authToken = resAuth.getBody().getAuthToken();
        this.userId = UUID.fromString(resAuth.getBody().getUserId());
    }

    @AfterEach
    void cleanChanges() {
        for (KeyType type : KeyType.values()) {
            apiKeysRepository.fetchApiKey(type, this.userId).ifPresent(apiKeysRepository::delete);
        }
    }

    @AfterAll
    void deleteTheUser() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAllUsersExceptSupperUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    private final KeyType keyType = KeyType.values()[0];

    private final RequestApiKey requestApiKey = new RequestApiKey("sk-test-1234567890-abcd", keyType);

    @Test
    public void createKeySuccessIT() {
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        HttpEntity<RequestApiKey> entityPut = new HttpEntity<>(requestApiKey, headers);

        ResponseEntity<ResponseApiKeyStatusMsg> res = restTemplate.exchange(
                baseUrl + "/ai/keys/update", HttpMethod.PUT, entityPut, ResponseApiKeyStatusMsg.class);

        ResponseApiKeyStatusMsg resBody = res.getBody();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("Successfully created key type: %s".formatted(keyType), resBody.getMsg());
        assertEquals(1, resBody.getListOfPresentKeys().size());

        // the key must be stored encrypted
        ApiKeysEntity stored = apiKeysRepository.fetchApiKey(keyType, this.userId).orElseThrow();
        assertNotEquals(requestApiKey.getRawKey(), stored.getApiKeyEncrypted());
        assertEquals("abcd", stored.getKeyLast4());
        assertTrue(stored.isActive());
    }

    @Test
    public void createThenUpdateKeySuccessIT() {
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        HttpEntity<RequestApiKey> entityPut = new HttpEntity<>(requestApiKey, headers);

        restTemplate.exchange(
                baseUrl + "/ai/keys/update", HttpMethod.PUT, entityPut, ResponseApiKeyStatusMsg.class);

        String encryptedBefore = apiKeysRepository.fetchApiKey(keyType, this.userId).orElseThrow().getApiKeyEncrypted();

        RequestApiKey updatedRequest = new RequestApiKey("sk-test-0987654321-wxyz", keyType);
        HttpEntity<RequestApiKey> entityPutUpdate = new HttpEntity<>(updatedRequest, headers);

        ResponseEntity<ResponseApiKeyStatusMsg> resUpdate = restTemplate.exchange(
                baseUrl + "/ai/keys/update", HttpMethod.PUT, entityPutUpdate, ResponseApiKeyStatusMsg.class);

        assertEquals(HttpStatus.OK, resUpdate.getStatusCode());
        assertEquals("Successfully updated key type: %s".formatted(keyType), resUpdate.getBody().getMsg());
        assertEquals(1, resUpdate.getBody().getListOfPresentKeys().size());

        ApiKeysEntity stored = apiKeysRepository.fetchApiKey(keyType, this.userId).orElseThrow();
        assertNotEquals(encryptedBefore, stored.getApiKeyEncrypted());
        assertEquals("wxyz", stored.getKeyLast4());
    }

    @Test
    public void createThenGetKeyStatusSuccessIT() {
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        HttpEntity<RequestApiKey> entityPut = new HttpEntity<>(requestApiKey, headers);

        ResponseEntity<ResponseApiKeyStatusMsg> baselineRes = restTemplate.exchange(
                baseUrl + "/ai/keys/status", HttpMethod.GET, entityGet, ResponseApiKeyStatusMsg.class);
        int baselineCount = baselineRes.getBody().getListOfPresentKeys().size();

        restTemplate.exchange(
                baseUrl + "/ai/keys/update", HttpMethod.PUT, entityPut, ResponseApiKeyStatusMsg.class);

        ResponseEntity<ResponseApiKeyStatusMsg> resGet = restTemplate.exchange(
                baseUrl + "/ai/keys/status", HttpMethod.GET, entityGet, ResponseApiKeyStatusMsg.class);

        assertEquals(HttpStatus.OK, resGet.getStatusCode());
        assertEquals(baselineCount + 1, resGet.getBody().getListOfPresentKeys().size());
    }

    @Test
    public void createThenDeleteKeySuccessIT() {
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        HttpEntity<String> entityGetOrDelete = new HttpEntity<>(headers);
        HttpEntity<RequestApiKey> entityPut = new HttpEntity<>(requestApiKey, headers);

        restTemplate.exchange(
                baseUrl + "/ai/keys/update", HttpMethod.PUT, entityPut, ResponseApiKeyStatusMsg.class);

        ResponseEntity<ResponseApiKeyStatusMsg> resDelete = restTemplate.exchange(
                baseUrl + "/ai/keys/delete/" + keyType.name(), HttpMethod.DELETE, entityGetOrDelete, ResponseApiKeyStatusMsg.class);

        assertEquals(HttpStatus.OK, resDelete.getStatusCode());
        assertEquals("Successfully deleted the key.", resDelete.getBody().getMsg());
        assertTrue(resDelete.getBody().getListOfPresentKeys().isEmpty());
        assertFalse(apiKeysRepository.fetchApiKey(keyType, this.userId).isPresent());
    }


}