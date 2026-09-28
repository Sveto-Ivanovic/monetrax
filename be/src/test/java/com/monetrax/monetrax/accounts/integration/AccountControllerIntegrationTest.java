package com.monetrax.monetrax.accounts.integration;

import com.monetrax.monetrax.accounts.dto.*;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.repository.RefreshTokenRepository;
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String authToken;
    private UUID userId;
    private String secondUserAuthToken;

    private ResponseEntity<AuthResponse> loginAs(String baseUrl, String email, String password) {
        AuthRequest req = AuthRequest.builder().email(email).password(password).build();
        return restTemplate.postForEntity(baseUrl + "/auth/login", req, AuthResponse.class);
    }

    private String extractRefreshToken(ResponseEntity<?> res) {
        List<String> cookies = res.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String prefix = "refreshToken=";
        return cookies.stream()
                .filter(c -> c.startsWith(prefix))
                .findFirst()
                .map(c -> c.substring(prefix.length(), c.indexOf(';') == -1 ? c.length() : c.indexOf(';')))
                .orElseThrow(() -> new AssertionError("No refreshToken cookie in response"));
    }

    private HttpEntity<Void> withRefreshCookie(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refreshToken=" + refreshToken);
        return new HttpEntity<>(headers);
    }

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

        UserCreation secondUserCreation = UserCreation.builder()
                .userEmail("jane.smith311@example.com")
                .userName("janesmith311")
                .name("Jane")
                .surname("Smith")
                .dateOfBirth(LocalDate.of(1992, 3, 21))
                .password("TestPass123!")
                .build();

        restTemplate.postForEntity(baseUrl + "/user/create", secondUserCreation, UserInformation.class);

        AuthRequest secondAuthRequest = AuthRequest.builder()
                .email("jane.smith311@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resSecondAuth = restTemplate.postForEntity(
                baseUrl + "/auth/login", secondAuthRequest, AuthResponse.class
        );
        assert resSecondAuth.getBody() != null;

        this.secondUserAuthToken = resSecondAuth.getBody().getAuthToken();
    }

    @AfterEach
    void cleanChanges(){
        accountRepository.deleteAll();
    }

    @AfterAll
    void deleteTheUser(){
        refreshTokenRepository.deleteAll();
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

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    private AccountInformation createAccount(String baseUrl, AccountCreate body, String token) {
        HttpEntity<AccountCreate> entityPost = new HttpEntity<>(body, authHeaders(token));
        ResponseEntity<AccountInformation> res = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, entityPost, AccountInformation.class);
        return res.getBody();
    }


    @Test
    public void createThenFetchUserAccountSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = authHeaders(this.authToken);

        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        AccountInformation resAccountInformation = createAccount(baseUrl, accountCreate, this.authToken);

        assert resAccountInformation != null;
        ResponseEntity<AccountInformation> resGet = restTemplate.exchange(
                baseUrl + "/accounts/account/"+resAccountInformation.getAccountId(), HttpMethod.GET, entityGet, AccountInformation.class);

        assertEquals(resGet.getBody(), resAccountInformation);
    }

    @Test
    public void createAccountMissingRequiredFieldsFailsValidationIT() {
        String baseUrl = "http://localhost:" + port;

        AccountCreate invalid = AccountCreate.builder()
                .name(null)
                .currency("EU")
                .institutionName("Hipot")
                .accountNumberMasked("4456")
                .initialBalance(new BigDecimal("0.00"))
                .build();

        HttpEntity<AccountCreate> entityPost = new HttpEntity<>(invalid, authHeaders(this.authToken));

        ResponseEntity<ErrorResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, entityPost, ErrorResponse.class);


        List<Map<String, Object>> expectedErrors = List.of(
                Map.of("message", "must not be blank", "clue", "name"),
                Map.of("message", "must not be null", "clue", "name"),
                Map.of("message", "size must be between 3 and 3", "clue", "currency")
                );

        ErrorResponse expectedResponse = new ErrorResponse(400, expectedErrors);
        assertEquals(expectedResponse.getErrors().stream().sorted(Comparator.comparing(Object::toString)).toList(),
                res.getBody().getErrors().stream().sorted(Comparator.comparing(Object::toString)).toList());
    }

    @Test
    public void createAccountWithoutAuthTokenFailsIT() {
        String baseUrl = "http://localhost:" + port;

        HttpEntity<AccountCreate> entityPost = new HttpEntity<>(accountCreate, new HttpHeaders());

        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, entityPost, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    public void fetchNonexistentAccountReturnsNotFoundIT() {
        String baseUrl = "http://localhost:" + port;
        HttpEntity<String> entityGet = new HttpEntity<>(authHeaders(this.authToken));

        ResponseEntity<ErrorResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/account/" + UUID.randomUUID(), HttpMethod.GET, entityGet, ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("No such account exists!","InvalidAccountID");
        ErrorResponse expectedResponse = new ErrorResponse(404,expectedErrors);
        assertEquals(expectedResponse,  res.getBody());
    }

    @Test
    public void fetchAnotherUsersAccountIsNotFoundIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation created = createAccount(baseUrl, accountCreate, this.authToken);
        assertNotNull(created);

        HttpEntity<String> entityGet = new HttpEntity<>(authHeaders(this.secondUserAuthToken));
        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId(), HttpMethod.GET, entityGet, String.class);

        assertTrue( res.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void fetchAllAccountsReturnsOnlyActiveByDefaultIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation active = createAccount(baseUrl, accountCreate, this.authToken);

        AccountCreate secondAccount = AccountCreate.builder()
                .accountNumberMasked("9911")
                .currency("USD")
                .description("Second account for archive testing.")
                .initialBalance(new BigDecimal("100.00"))
                .institutionName("Second Bank")
                .name("Second Tracking Account")
                .build();
        AccountInformation toArchive = createAccount(baseUrl, secondAccount, this.authToken);

        HttpEntity<AccountArchive> archiveEntity = new HttpEntity<>(new AccountArchive(true), authHeaders(this.authToken));

        restTemplate.exchange(
                baseUrl + "/accounts/account/" + toArchive.getAccountId() + "/update/archive",
                HttpMethod.PATCH, archiveEntity, AccountInformation.class);

        HttpEntity<String> entityGet = new HttpEntity<>(authHeaders(this.authToken));
        ResponseEntity<AccountListResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/all", HttpMethod.GET, entityGet, AccountListResponse.class);

        AccountListResponse body = res.getBody();
        assertNotNull(body);
        List<AccountInformation> accounts = body.getEntityList();
        assertEquals(1, accounts.size());
        assertEquals(active.getAccountId(), accounts.get(0).getAccountId());
    }

    @Test
    public void fetchAllAccountsIncludeArchiveReturnsBothIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation active = createAccount(baseUrl, accountCreate, this.authToken);

        AccountCreate secondAccount = AccountCreate.builder()
                .accountNumberMasked("9911")
                .currency("USD")
                .description("Second account for archive testing.")
                .initialBalance(new BigDecimal("100.00"))
                .institutionName("Second Bank")
                .name("Second Tracking Account")
                .build();
        AccountInformation toArchive = createAccount(baseUrl, secondAccount, this.authToken);

        HttpEntity<AccountArchive> archiveEntity = new HttpEntity<>(new AccountArchive(true), authHeaders(this.authToken));
        restTemplate.exchange(
                baseUrl + "/accounts/account/" + toArchive.getAccountId() + "/update/archive",
                HttpMethod.PATCH, archiveEntity, AccountInformation.class);

        HttpEntity<String> entityGet = new HttpEntity<>(authHeaders(this.authToken));
        ResponseEntity<AccountListResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/all?includeArchive=true", HttpMethod.GET, entityGet, AccountListResponse.class);

        AccountListResponse body = res.getBody();
        assertNotNull(body);
        assertEquals(2, body.getEntityList().size());
    }

    @Test
    public void fetchArchivedAccountsReturnsOnlyArchivedIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation toArchive = createAccount(baseUrl, accountCreate, this.authToken);

        HttpEntity<AccountArchive> archiveEntity = new HttpEntity<>(
                AccountArchive.builder().archived(true).build(), authHeaders(this.authToken));
        restTemplate.exchange(
                baseUrl + "/accounts/account/" + toArchive.getAccountId() + "/update/archive",
                HttpMethod.PATCH, archiveEntity, AccountInformation.class);

        HttpEntity<String> entityGet = new HttpEntity<>(authHeaders(this.authToken));
        ResponseEntity<AccountListResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/all/archived", HttpMethod.GET, entityGet, AccountListResponse.class);

        AccountListResponse body = res.getBody();
        assertNotNull(body);
        assertEquals(1, body.getEntityList().size());
        assertEquals(toArchive.getAccountId(), body.getEntityList().get(0).getAccountId());
        assertFalse(body.getEntityList().get(0).isActive());
    }

    @Test
    public void updateAccountChangesFieldsIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation created = createAccount(baseUrl, accountCreate, this.authToken);

        AccountUpdate update = AccountUpdate.builder()
                .name("Renamed Account")
                .description("Updated description text.")
                .institutionName("New Institution")
                .accountNumberMasked("1234")
                .toggleActivate(false)
                .build();

        HttpEntity<AccountUpdate> updateEntity = new HttpEntity<>(update, authHeaders(this.authToken));
        ResponseEntity<AccountInformation> res = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId() + "/update",
                HttpMethod.PUT, updateEntity, AccountInformation.class);

        AccountInformation updated = res.getBody();
        assertNotNull(updated);
        assertEquals("Renamed Account", updated.getName());
        assertEquals("Updated description text.", updated.getDescription());
        assertEquals("New Institution", updated.getInstitutionName());
        assertEquals("1234", updated.getAccountNumberMasked());
        assertEquals(false, updated.isActive());
        assertEquals(created.getAccountId(), updated.getAccountId());
    }

    @Test
    public void updateAccountInvalidFieldFailsValidationIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation created = createAccount(baseUrl, accountCreate, this.authToken);

        AccountUpdate invalidUpdate = AccountUpdate.builder()
                .name("abc")
                .build();

        HttpEntity<AccountUpdate> updateEntity = new HttpEntity<>(invalidUpdate, authHeaders(this.authToken));
        ResponseEntity<ErrorResponse> res = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId() + "/update",
                HttpMethod.PUT, updateEntity, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

        List<Map<String, Object>> expectedErrors = List.of(
                Map.of("message", "size must be between 5 and 100", "clue", "name")
        );

        ErrorResponse expectedResponse = new ErrorResponse(400, expectedErrors);
        assertEquals(expectedResponse.getErrors(),
                res.getBody().getErrors());
    }

    @Test
    public void archiveThenUnarchiveAccountTogglesActiveFlagIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation created = createAccount(baseUrl, accountCreate, this.authToken);
        assertTrue(created.isActive());

        HttpEntity<AccountArchive> archiveEntity = new HttpEntity<>(
                AccountArchive.builder().archived(true).build(), authHeaders(this.authToken));
        ResponseEntity<AccountInformation> archivedRes = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId() + "/update/archive",
                HttpMethod.PATCH, archiveEntity, AccountInformation.class);

        assertNotNull(archivedRes.getBody());
        assertFalse(archivedRes.getBody().isActive());

        HttpEntity<AccountArchive> unarchiveEntity = new HttpEntity<>(
                AccountArchive.builder().archived(false).build(), authHeaders(this.authToken));
        ResponseEntity<AccountInformation> unarchivedRes = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId() + "/update/archive",
                HttpMethod.PATCH, unarchiveEntity, AccountInformation.class);

        assertNotNull(unarchivedRes.getBody());
        assertFalse(unarchivedRes.getBody().isActive());
    }

    @Test
    public void updateAnotherUsersAccountIsForbiddenOrNotFoundIT() {
        String baseUrl = "http://localhost:" + port;

        AccountInformation created = createAccount(baseUrl, accountCreate, this.authToken);

        AccountUpdate update = AccountUpdate.builder()
                .name("Hijacked Name")
                .build();

        HttpEntity<AccountUpdate> updateEntity = new HttpEntity<>(update, authHeaders(this.secondUserAuthToken));
        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl + "/accounts/account/" + created.getAccountId() + "/update",
                HttpMethod.PUT, updateEntity, String.class);

        assertTrue(res.getStatusCode() == HttpStatus.NOT_FOUND);
    }


    // Auth flow starts from here

    @Test
    public void loginWithValidCredentialsReturnsJwtAndRefreshCookieIT() {
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<AuthResponse> res = loginAs(baseUrl, "john.doe742@example.com", "TestPass123!");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertNotNull(res.getBody().getAuthToken());
        assertEquals(this.userId.toString(), res.getBody().getUserId());

        String setCookie = res.getHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .filter(c->c.startsWith("refreshToken=")).findFirst().orElse(null);
        assertNotNull(setCookie);
        assertTrue(setCookie.startsWith("refreshToken="));
        assertTrue(setCookie.contains("HttpOnly"));
        assertTrue(setCookie.contains("Secure"));
        log.info(setCookie);
        assertTrue(setCookie.toLowerCase().contains("samesite=strict"));
        assertTrue(setCookie.contains("Path=/auth/token"));
        assertFalse(extractRefreshToken(res).isBlank());
    }

    @Test
    public void loginWithWrongPasswordFailsIT() {
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                AuthRequest.builder().email("john.doe742@example.com").password("WrongPass999!").build(),
                String.class);

        assertTrue(res.getStatusCode().is4xxClientError());
        assertNull(res.getHeaders().get(HttpHeaders.SET_COOKIE));
    }

    @Test
    public void refreshTokenReturnsNewJwtAndRotatesCookieIT() {
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<AuthResponse> loginRes = loginAs(baseUrl, "john.doe742@example.com", "TestPass123!");
        String oldRefresh = extractRefreshToken(loginRes);

        ResponseEntity<AuthResponse> refreshRes = restTemplate.exchange(
                baseUrl + "/auth/token/refresh", HttpMethod.POST,
                withRefreshCookie(oldRefresh), AuthResponse.class);

        assertEquals(HttpStatus.OK, refreshRes.getStatusCode());
        assertNotNull(refreshRes.getBody());
        assertNotNull(refreshRes.getBody().getAuthToken());
        assertEquals(this.userId.toString(), refreshRes.getBody().getUserId());

        String newRefresh = extractRefreshToken(refreshRes);
        assertNotEquals(oldRefresh, newRefresh);
    }

    @Test
    public void refreshWithInvalidTokenFailsIT() {
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl + "/auth/token/refresh", HttpMethod.POST,
                withRefreshCookie("thisTokenDoesNotExist123"), String.class);

        log.info(res.getBody());
        assertTrue(res.getStatusCode().is4xxClientError());
    }

    @Test
    public void logoutRevokesRefreshTokenAndClearsCookieIT() {
        String baseUrl = "http://localhost:" + port;

        String refresh = extractRefreshToken(loginAs(baseUrl, "john.doe742@example.com", "TestPass123!"));

        ResponseEntity<String> logoutRes = restTemplate.exchange(
                baseUrl + "/auth/token/logout", HttpMethod.POST,
                withRefreshCookie(refresh), String.class);

        assertEquals(HttpStatus.OK, logoutRes.getStatusCode());
        assertEquals("Successfully logged out.", logoutRes.getBody());

        String setCookie = logoutRes.getHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .filter(c->c.startsWith("refreshToken=")).findFirst().orElse(null);
        assertNotNull(setCookie);
        log.info(setCookie);
        assertTrue(setCookie.contains("refreshToken=;"));

        // the revoked token can no longer be used to refresh
        ResponseEntity<String> refreshRes = restTemplate.exchange(
                baseUrl + "/auth/token/refresh", HttpMethod.POST,
                withRefreshCookie(refresh), String.class);
        assertTrue(refreshRes.getStatusCode().is4xxClientError());
    }


}
