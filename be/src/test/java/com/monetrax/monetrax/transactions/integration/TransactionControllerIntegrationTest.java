package com.monetrax.monetrax.transactions.integration;

import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.dto.FetchAllCategoriesResponse;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.transactions.dto.RequestedCategoryInformation;
import com.monetrax.monetrax.transactions.dto.TransactionCreate;
import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionInformation;
import com.monetrax.monetrax.transactions.repository.TransactionAdditionalInfoRepository;
import com.monetrax.monetrax.transactions.repository.TransactionCategoriesRepository;
import com.monetrax.monetrax.transactions.repository.TransactionLineItemsRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionCategoriesRepository transactionCategoriesRepository;

    @Autowired
    private TransactionAdditionalInfoRepository transactionAdditionalInfoRepository;

    @Autowired
    private TransactionLineItemsRepository transactionLineItemsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private UUID userId;

    private UUID accountId;
    private String accountCurrency;
    private List<CategoryInformation> listOfCategoriesThatAreNotDefault;
    private List<CategoryInformation> listOfAllAvailableCategories;

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

        restTemplate.postForEntity(baseUrl + "/user/create", userCreation, UserInformation.class);

        AuthRequest authRequest = AuthRequest.builder()
                .email("john.doe742@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl + "/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        this.authToken = resAuth.getBody().getAuthToken();
        this.userId = UUID.fromString(resAuth.getBody().getUserId());
    }

    @BeforeEach
    void setUpAccountAndCategory() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        // create an account to attach transactions to
        AccountCreate accountCreate = AccountCreate.builder()
                .accountNumberMasked("4456")
                .currency("EUR")
                .description("Tracking my personal everyday account.")
                .initialBalance(new BigDecimal("200.00"))
                .institutionName("Hipot")
                .name("General Tracking Account")
                .build();

        HttpEntity<AccountCreate> accountEntity = new HttpEntity<>(accountCreate, headers);
        ResponseEntity<AccountInformation> accountRes = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, accountEntity, AccountInformation.class);

        AccountInformation createdAccount = accountRes.getBody();
        assertNotNull(createdAccount);
        this.accountId = createdAccount.getAccountId();
        this.accountCurrency = accountCreate.getCurrency();

        // create two new categories tied to the account
        CategoryCreate categoryCreate1 =  CategoryCreate.builder()
                .categoryType(CategoryKind.EXPENSE)
                .name("Voli Store Purchase")
                .description("Category for purchasing everyday supplies in Voli store.")
                .build();

        CategoryCreate categoryCreate2 =  CategoryCreate.builder()
                .categoryType(CategoryKind.INCOME)
                .name("Monthly Payday at X")
                .description("Category for monthly payments from X social media account.")
                .build();


        HttpEntity<CategoryCreate> entityPost1 = new HttpEntity<>(categoryCreate1, headers);
        HttpEntity<CategoryCreate> entityPost2 = new HttpEntity<>(categoryCreate2, headers);

        ResponseEntity<CategoryInformation> res1 = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost1, CategoryInformation.class);
        ResponseEntity<CategoryInformation> res2 = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost2, CategoryInformation.class);

        // now fetch all categories
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<FetchAllCategoriesResponse> categoriesRes = restTemplate.exchange(
                baseUrl + "/categories/all", HttpMethod.GET, entityGet, FetchAllCategoriesResponse.class);

        FetchAllCategoriesResponse categoriesBody = categoriesRes.getBody();
        assertNotNull(categoriesBody);
        assertFalse(categoriesBody.getCategories().isEmpty());

        this.listOfAllAvailableCategories = categoriesBody.getCategories();

        assert res1.getBody() != null;
        assert res2.getBody() != null;
        this.listOfCategoriesThatAreNotDefault = new ArrayList<>(List.of(res1.getBody(), res2.getBody()));
    }

    @AfterEach
    void cleanChanges() {
        transactionAdditionalInfoRepository.deleteAll();
        transactionLineItemsRepository.deleteAll();
        transactionCategoriesRepository.deleteAll();
        transactionRepository.deleteAll();
        categoryRepository.deleteAllNoneDefaultCategories();
        accountRepository.deleteAll();
    }

    @AfterAll
    void deleteTheUser() {
        userRepository.deleteAllUsersExceptSupperUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        return headers;
    }


    @Test
    public void createThenFetchTransactionSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();
        // this one is expense
        requestedCategoryInformationList.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(0).getName()));

        TransactionCreate transactionCreate = TransactionCreate.builder()
                .name("Voli Store Purchase at 09/15/2026")
                .description("Everyday supplies purchase at Voli store.")
                .amount(new BigDecimal("25.50"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();

        HttpEntity<TransactionCreate> entityPost = new HttpEntity<>(transactionCreate, headers);
        ResponseEntity<TransactionCreateUpdateResponse> res = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost, TransactionCreateUpdateResponse.class);

        TransactionCreateUpdateResponse createResponse = res.getBody();
        assertNotNull(createResponse);
        UUID transactionId = createResponse.getTransactionId();
        assertNotNull(transactionId);

        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<TransactionInformation> resGet = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId + "/fetch",
                HttpMethod.GET, entityGet, TransactionInformation.class);

        TransactionInformation fetched = resGet.getBody();

        ResponseEntity<AccountInformation> resGetAcc = restTemplate.exchange(
                baseUrl + "/accounts/account/"+this.accountId, HttpMethod.GET, entityGet, AccountInformation.class);

        log.info(fetched.toString());
        assertNotNull(fetched);
        assertEquals(transactionId, fetched.getTransactionId());
        assertEquals(transactionCreate.getName(), fetched.getName());
        assertEquals(transactionCreate.getDescription(), fetched.getDescription());
        assertEquals(0, transactionCreate.getAmount().compareTo(fetched.getAmount()));
        assertEquals(transactionCreate.getCurrency(), fetched.getCurrency());
        assertEquals(1, fetched.getCategories().size());
        assertEquals(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(), fetched.getCategories().get(0).getCategoryId());
        assertEquals((new BigDecimal("200.00")).subtract(new BigDecimal("25.50")), resGetAcc.getBody().getCurrentBalance());

    }

}
