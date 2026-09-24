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
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.AdjustmentKind;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void createFetchAndUpdateTransactionSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();

        // this one is income, but two selected categories that match
        requestedCategoryInformationList.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(1).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(1).getName()));
        var cat2 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Business Income"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList.add(new RequestedCategoryInformation(cat2.getCategoryId(), cat2.getName()));

        TransactionCreate transactionCreate = TransactionCreate.builder()
                .name("income")
                .description("generous income")
                .amount(new BigDecimal("1000"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of(new TransactionAdditionalInfoCreate(AdjustmentKind.ADDITION,"income from company x",new BigDecimal("1000.00"))))
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

        assert resGet.getBody() != null;
        assertEquals(2, resGet.getBody().getCategories().size());

        // update
        TransactionUpdate transactionUpdate = TransactionUpdate.builder()
                .amount(new BigDecimal("800.00"))
                .description("they lowered my salary!")
                .categories(List.of(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(1).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(1).getName())))
                .build();

        HttpEntity<TransactionUpdate> entityPut = new HttpEntity<>(transactionUpdate, headers);
        ResponseEntity<TransactionCreateUpdateResponse> resPut = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" +transactionId + "/update",
                HttpMethod.PUT, entityPut, TransactionCreateUpdateResponse.class);


        ResponseEntity<TransactionInformation> resGet2 = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId + "/fetch",
                HttpMethod.GET, entityGet, TransactionInformation.class);

        assert resGet2.getBody() != null;
        assertEquals(1, resGet2.getBody().getCategories().size());
        assertEquals("they lowered my salary!", resGet2.getBody().getDescription());

        ResponseEntity<AccountInformation> resGetAcc = restTemplate.exchange(
                baseUrl + "/accounts/account/"+this.accountId, HttpMethod.GET, entityGet, AccountInformation.class);

        assertEquals((new BigDecimal("200.00")).add(new BigDecimal("800.00")), resGetAcc.getBody().getCurrentBalance());
    }

    @Test
    public void createFetchAndDeleteTransactionSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();
        // this one is expense
        requestedCategoryInformationList.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(1).getName()));
        var cat2 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Utilities"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList.add(new RequestedCategoryInformation(cat2.getCategoryId(), cat2.getName()));

        TransactionCreate transactionCreate = TransactionCreate.builder()
                .name("Voli Store Purchase at 09/15/2026")
                .description("Everyday supplies purchase at Voli store.")
                .amount(new BigDecimal("3.00"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of())
                .lineInformation(List.of(TransactionLineItemsCreate.builder().productName("Coca cola 1.5L").amount(new BigDecimal("3.00")).build()))
                .build();

        HttpEntity<TransactionCreate> entityPost = new HttpEntity<>(transactionCreate, headers);
        ResponseEntity<TransactionCreateUpdateResponse> res = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost, TransactionCreateUpdateResponse.class);

        TransactionCreateUpdateResponse createResponse = res.getBody();
        assertNotNull(createResponse);
        UUID transactionId = createResponse.getTransactionId();
        assertNotNull(transactionId);

        // get
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<TransactionInformation> resGet = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId + "/fetch",
                HttpMethod.GET, entityGet, TransactionInformation.class);

        assert resGet.getBody() != null;
        assertEquals(2, resGet.getBody().getCategories().size());


        // delete
        HttpEntity<String> entityDelete = new HttpEntity<>(headers);
        ResponseEntity<TransactionCreateUpdateResponse> resDelete = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId + "/delete",
                HttpMethod.DELETE, entityDelete, TransactionCreateUpdateResponse.class);

        // check

        ResponseEntity<ErrorResponse> resGet2 = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId + "/fetch",
                HttpMethod.GET, entityGet, ErrorResponse.class);

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("No such transaction found !","MissingTransactionLikeEntity");
        ErrorResponse expectedResponse = new ErrorResponse(400,expectedErrors);

        assertEquals(expectedResponse, resGet2.getBody());


        ResponseEntity<AccountInformation> resGetAcc = restTemplate.exchange(
                baseUrl + "/accounts/account/"+this.accountId, HttpMethod.GET, entityGet, AccountInformation.class);
        assertEquals((new BigDecimal("200.00")), resGetAcc.getBody().getCurrentBalance());

    }

    @Test
    public void createTwoTransactionsAndFetchAllTransactionSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();
        requestedCategoryInformationList.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(1).getName()));
        var cat2 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Utilities"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList.add(new RequestedCategoryInformation(cat2.getCategoryId(), cat2.getName()));

        List<RequestedCategoryInformation> requestedCategoryInformationList2 = new ArrayList<>();
        requestedCategoryInformationList2.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(1).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(1).getName()));
        var cat3 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Business Income"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList2.add(new RequestedCategoryInformation(cat3.getCategoryId(), cat3.getName()));

        TransactionCreate transactionCreate1 = TransactionCreate.builder()
                .name("Voli Store Purchase at 09/15/2026")
                .description("Everyday supplies purchase at Voli store.")
                .amount(new BigDecimal("25.00"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();


        TransactionCreate transactionCreate2 = TransactionCreate.builder()
                .name("Random Income")
                .description("Income received from business")
                .amount(new BigDecimal("900.00"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList2)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();

        HttpEntity<TransactionCreate> entityPost = new HttpEntity<>(transactionCreate1, headers);
        HttpEntity<TransactionCreate> entityPost2 = new HttpEntity<>(transactionCreate2, headers);

        ResponseEntity<TransactionCreateUpdateResponse> res = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost, TransactionCreateUpdateResponse.class);

        ResponseEntity<String> res2 = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost2, String.class);


        // get
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<ListOfAccountTransactions> resGet = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/fetch",
                HttpMethod.GET, entityGet, ListOfAccountTransactions.class);

        assert resGet.getBody() != null;
        assertEquals(2, resGet.getBody().getTransactions().size());

        ResponseEntity<AccountInformation> resGetAcc = restTemplate.exchange(
                baseUrl + "/accounts/account/"+this.accountId, HttpMethod.GET, entityGet, AccountInformation.class);
        assertEquals((new BigDecimal("200.00").add(new BigDecimal("900")).subtract(new BigDecimal("25.00"))), resGetAcc.getBody().getCurrentBalance());
    }


    @Test
    public void createTransactionWithRecurrenceRuleFetchRulesAndDeleteRuleSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();
        // this one is expense
        requestedCategoryInformationList.add(new RequestedCategoryInformation(
                this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(),
                this.listOfCategoriesThatAreNotDefault.get(0).getName()));

        TransactionRecurrenceRule recurrenceRule = TransactionRecurrenceRule.builder()
                .ruleType(TransactionRecurrenceType.MONTH)
                .recurrenceNum(1)
                .maxNumOfOccurrencesAllowed(12)
                .build();

        TransactionCreate transactionCreate = TransactionCreate.builder()
                .name("Monthly Voli subscription")
                .description("Recurring monthly purchase at Voli store.")
                .amount(new BigDecimal("10.00"))
                .currency(this.accountCurrency)
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .transactionRecurrenceRule(recurrenceRule)
                .build();

        // create
        HttpEntity<TransactionCreate> entityPost = new HttpEntity<>(transactionCreate, headers);
        ResponseEntity<TransactionCreateUpdateResponse> res = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost, TransactionCreateUpdateResponse.class);

        TransactionCreateUpdateResponse createResponse = res.getBody();
        assertNotNull(createResponse);
        UUID transactionId = createResponse.getTransactionId();
        assertNotNull(transactionId);

        // fetch all recurrence rules for the account
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<TransactionRecurrenceResponse> resRules = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction-recurrence-rule/fetch",
                HttpMethod.GET, entityGet, TransactionRecurrenceResponse.class);

        assertNotNull(resRules.getBody());
        assertEquals(1, resRules.getBody().getList().size());

        TransactionRecurrenceInformation ruleInfo = resRules.getBody().getList().get(0);
        assertNotNull(ruleInfo.getRecurrenceRuleId());
        assertEquals(transactionId, ruleInfo.getTransactionId());
        assertEquals(TransactionRecurrenceType.MONTH, ruleInfo.getRecurrenceUnit());
        assertEquals(1, ruleInfo.getIntervalCount());
        assertEquals(12, ruleInfo.getMaxOccurrences());
        assertEquals(0, ruleInfo.getOccurrencesGenerated());
        assertEquals(LocalDate.now(ZoneOffset.UTC).plusMonths(1), ruleInfo.getNextRunDate());

        // delete the rule
        HttpEntity<String> entityDelete = new HttpEntity<>(headers);
        ResponseEntity<TransactionCreateUpdateResponse> resDelete = restTemplate.exchange(
                baseUrl + "/transactions/transaction/" + transactionId
                        + "/transaction-recurrence-rule/" + ruleInfo.getRecurrenceRuleId(),
                HttpMethod.DELETE, entityDelete, TransactionCreateUpdateResponse.class);

        assertEquals(200, resDelete.getStatusCode().value());
        assertNotNull(resDelete.getBody());
        assertEquals("Successfully deleted the rule", resDelete.getBody().getMsg());

        // fetch again, there should be no rules left
        ResponseEntity<TransactionRecurrenceResponse> resRulesAfterDelete = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction-recurrence-rule/fetch",
                HttpMethod.GET, entityGet, TransactionRecurrenceResponse.class);

        assertNotNull(resRulesAfterDelete.getBody());
        assertTrue(resRulesAfterDelete.getBody().getList().isEmpty());
    }
}
