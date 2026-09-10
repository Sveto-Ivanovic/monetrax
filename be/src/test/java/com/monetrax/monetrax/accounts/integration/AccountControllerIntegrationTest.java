package com.monetrax.monetrax.accounts.integration;

import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private UUID userId;

    @BeforeAll
    void authenticate() {
        String baseUrl = "http://localhost:" + port;

        UserCreation userCreation = UserCreation.builder()
                .userEmail("john.doe742@example.com")
                .userName("johndoe742")
                .name("John")
                .surname("Doe")
                .dateOfBirth(LocalDate.of(1995, 7, 14))
                .password("TestPass123!")
                .build();

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

        this.authToken = resAuth.getBody().getAuthToken();
        this.userId = UUID.fromString(resAuth.getBody().getUserId());
    }

    @AfterEach
    void cleanChanges(){
        accountRepository.deleteAll();
    }

    @AfterAll
    void deleteTheUser(){
        userRepository.deleteAllUsersExceptSupperUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    private AccountCreate accountCreate = AccountCreate.builder()
            .accountNumberMasked("4456")
            .currency("EUR")
            .description("Tracking my personal everyday account.")
            .initialBalance(new BigDecimal("0.00"))
            .institutionName("Hipot")
            .name("General Tracking Account")
            .build();


    @Test
    public void createThenFetchUserAccountSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        HttpEntity<AccountCreate> entityPost = new HttpEntity<>(accountCreate, headers);

        ResponseEntity<AccountInformation> res = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, entityPost, AccountInformation.class);

        AccountInformation resAccountInformation = res.getBody();

        assert resAccountInformation != null;
        ResponseEntity<AccountInformation> resGet = restTemplate.exchange(
                baseUrl + "/accounts/account/"+resAccountInformation.getAccountId(), HttpMethod.GET, entityGet, AccountInformation.class);

        assertEquals(resGet.getBody(), resAccountInformation);
    }

}
