package com.monetrax.monetrax.alert.integration;


import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.alerts.dto.*;
import com.monetrax.monetrax.alerts.entity.RuleType;
import com.monetrax.monetrax.alerts.repository.AlertConditionRepository;
import com.monetrax.monetrax.alerts.repository.AlertRepository;
import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.dto.FetchAllCategoriesResponse;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.transactions.dto.RequestedCategoryInformation;
import com.monetrax.monetrax.transactions.dto.TransactionCreate;
import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlertControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertConditionRepository alertConditionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionCategoriesRepository transactionCategoriesRepository;

    @Autowired
    private TransactionAdditionalInfoRepository transactionAdditionalInfoRepository;

    @Autowired
    private TransactionLineItemsRepository transactionLineItemsRepository;

    private String authToken;
    private UUID userId;

    private UUID accountId;
    private List<CategoryInformation> listOfCategoriesThatAreNotDefault;
    private List<CategoryInformation> listOfAllAvailableCategories;
    private UUID categoryGroceriesId;
    private UUID categoryUtilitesId;
    private UUID categoryIncomeId;

    @BeforeAll
    void authenticate() {
        String baseUrl = "http://localhost:" + port;

        UserCreation userCreation = UserCreation.builder()
                .userEmail("jane.doe931@example.com")
                .userName("janedoe931")
                .name("Jane")
                .surname("Doe")
                .dateOfBirth(LocalDate.of(1994, 3, 21))
                .password("TestPass123!")
                .build();

        restTemplate.postForEntity(baseUrl + "/user/create", userCreation, UserInformation.class);

        AuthRequest authRequest = AuthRequest.builder()
                .email("jane.doe931@example.com")
                .password("TestPass123!")
                .build();

        ResponseEntity<AuthResponse> resAuth = restTemplate.postForEntity(
                baseUrl + "/auth/login", authRequest, AuthResponse.class
        );
        assert resAuth.getBody() != null;

        this.authToken = resAuth.getBody().getAuthToken();
        this.userId = UUID.fromString(resAuth.getBody().getUserId());

        HttpHeaders headers = authHeaders();

        // create an account to attach alerts to
        AccountCreate accountCreate = AccountCreate.builder()
                .accountNumberMasked("7781")
                .currency("EUR")
                .description("Tracking my personal everyday account.")
                .initialBalance(new BigDecimal("500.00"))
                .institutionName("Hipot")
                .name("General Tracking Account")
                .build();

        HttpEntity<AccountCreate> accountEntity = new HttpEntity<>(accountCreate, headers);
        ResponseEntity<AccountInformation> accountRes = restTemplate.exchange(
                baseUrl + "/accounts/create", HttpMethod.POST, accountEntity, AccountInformation.class);

        AccountInformation createdAccount = accountRes.getBody();
        assertNotNull(createdAccount);
        this.accountId = createdAccount.getAccountId();

        // create two new categories tied to the account
        CategoryCreate categoryCreate1 = CategoryCreate.builder()
                .categoryType(CategoryKind.EXPENSE)
                .name("Groceries Spending")
                .description("Category for tracking grocery spending.")
                .build();


        HttpEntity<CategoryCreate> entityPost1 = new HttpEntity<>(categoryCreate1, headers);

        ResponseEntity<CategoryInformation> res1 = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost1, CategoryInformation.class);

        // now fetch all categories
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<FetchAllCategoriesResponse> categoriesRes = restTemplate.exchange(
                baseUrl + "/categories/all", HttpMethod.GET, entityGet, FetchAllCategoriesResponse.class);

        FetchAllCategoriesResponse categoriesBody = categoriesRes.getBody();
        assertNotNull(categoriesBody);
        assertFalse(categoriesBody.getCategories().isEmpty());

        this.listOfAllAvailableCategories = categoriesBody.getCategories();

        assert res1.getBody() != null;

        this.listOfCategoriesThatAreNotDefault = new ArrayList<>(List.of(res1.getBody()));

        // create bunch of transactions in the account
        List<RequestedCategoryInformation> requestedCategoryInformationList = new ArrayList<>();
        // this one is expense
        var cat2 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Utilities"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList.add(new RequestedCategoryInformation(cat2.getCategoryId(), cat2.getName()));
        this.categoryUtilitesId = cat2.getCategoryId();

        TransactionCreate transactionCreate = TransactionCreate.builder()
                .name("Garden tool purchase in OKOV")
                .description("Purchasing garden related tools in OKOV, mostly lawn mower and tools connected to it.")
                .amount(new BigDecimal("320.35"))
                .currency(accountEntity.getBody().getCurrency())
                .categories(requestedCategoryInformationList)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();


        List<RequestedCategoryInformation> requestedCategoryInformationList2 = new ArrayList<>();
        requestedCategoryInformationList2.add(new RequestedCategoryInformation(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId(), this.listOfCategoriesThatAreNotDefault.get(0).getName()));
        requestedCategoryInformationList2.add(new RequestedCategoryInformation(cat2.getCategoryId(), cat2.getName()));

        this.categoryGroceriesId = this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId();

        TransactionCreate transactionCreate1 = TransactionCreate.builder()
                .name("Voli Store Purchase")
                .description("Everyday supplies purchase at Voli store.")
                .amount(new BigDecimal("50.50"))
                .currency(accountEntity.getBody().getCurrency())
                .categories(requestedCategoryInformationList2)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();

        List<RequestedCategoryInformation> requestedCategoryInformationList3 = new ArrayList<>();

        var cat3 = this.listOfAllAvailableCategories.stream()
                .filter(x->x.getName().equals("Business Income"))
                .collect(Collectors.toSet())
                .iterator().next();
        requestedCategoryInformationList3.add(new RequestedCategoryInformation(cat3.getCategoryId(), cat3.getName()));
        this.categoryIncomeId = cat3.getCategoryId();

        TransactionCreate transactionCreate2 = TransactionCreate.builder()
                .name("Salary Monthly")
                .description("Monthly salary.")
                .amount(new BigDecimal("920"))
                .currency(accountEntity.getBody().getCurrency())
                .categories(requestedCategoryInformationList3)
                .additionalInfo(List.of())
                .lineInformation(List.of())
                .build();


        HttpEntity<TransactionCreate> entityPost3 = new HttpEntity<>(transactionCreate, headers);
        HttpEntity<TransactionCreate> entityPost4 = new HttpEntity<>(transactionCreate1, headers);
        HttpEntity<TransactionCreate> entityPost5 = new HttpEntity<>(transactionCreate2, headers);


        ResponseEntity<TransactionCreateUpdateResponse> res3 = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost3, TransactionCreateUpdateResponse.class);

        ResponseEntity<TransactionCreateUpdateResponse> res4 = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost4, TransactionCreateUpdateResponse.class);

        ResponseEntity<TransactionCreateUpdateResponse> res5 = restTemplate.exchange(
                baseUrl + "/transactions/account/" + this.accountId + "/transaction/create",
                HttpMethod.POST, entityPost5, TransactionCreateUpdateResponse.class);

        assertNotNull(res3.getBody());
        assertNotNull(res4.getBody());
        assertNotNull(res5.getBody());

    }

    @AfterEach
    void cleanChanges() {
        alertConditionRepository.deleteAll();
        alertRepository.deleteAll();
    }

    @AfterAll
    void deleteTheUser() {
        transactionAdditionalInfoRepository.deleteAll();
        transactionLineItemsRepository.deleteAll();
        transactionCategoriesRepository.deleteAll();
        transactionRepository.deleteAll();
        categoryRepository.deleteAllNoneDefaultCategories();
        accountRepository.deleteAll();
        userRepository.deleteAllUsersExceptSupperUser(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.authToken);
        return headers;
    }

    @Test
    public void createThenFetchAlertWithoutRecurrenceSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        AlertConditionCreation conditionCreation = AlertConditionCreation.builder()
                .categoryId(this.categoryGroceriesId)
                .ruleType(RuleType.GREATER_OR_EQUAL)
                .limitValueHigh(new BigDecimal("50.00"))
                .build();

        AlertConditionCreation conditionCreation2 = AlertConditionCreation.builder()
                .categoryId(this.categoryUtilitesId)
                .ruleType(RuleType.GREATER_OR_EQUAL)
                .limitValueHigh(new BigDecimal("300.00"))
                .build();

        AlertCreate alertCreate = AlertCreate.builder()
                .name("Groceries/Utilities Budget Alert")
                .description("Alert me when groceries spending is breaches limit.")
                .dateFrom(LocalDate.now().minusDays(3))
                .dateTo(LocalDate.now().plusDays(30))
                .filtersToCreate(List.of(conditionCreation, conditionCreation2))
                .build();

        HttpEntity<AlertCreate> entityPost = new HttpEntity<>(alertCreate, headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> res = restTemplate.exchange(
                baseUrl + "/alerts/account/" + this.accountId + "/alert/create",
                HttpMethod.POST, entityPost, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse createResponse = res.getBody();
        assertNotNull(createResponse);

        UUID alertId = createResponse.getId();
        assertNotNull(alertId);

        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<AlertInformation> resGet = restTemplate.exchange(
                baseUrl + "/alerts/alert/" + alertId + "/fetch",
                HttpMethod.GET, entityGet, AlertInformation.class);


        AlertInformation fetched = resGet.getBody();

        assertNotNull(fetched);
        assertEquals(alertId, fetched.getAlertId());
        assertEquals(alertCreate.getName(), fetched.getName());
        assertEquals(alertCreate.getDescription(), fetched.getDescription());
        assertEquals(alertCreate.getDateFrom(), fetched.getDateFrom());
        assertEquals(alertCreate.getDateTo(), fetched.getDateTo());
        assertEquals(2, fetched.getFilters().size());
        assertTrue(fetched.isActive());
        assertTrue(fetched.isBreached());

        assertNotNull(
                fetched.getAlertStates().stream()
                        .filter(x -> x.getCategoryId().equals(this.categoryUtilitesId))
                        .findFirst()
                        .get()
                        .getAmount()
        );
        assertNotNull(
                fetched.getAlertStates().stream()
                        .filter(x -> x.getCategoryId().equals(this.categoryGroceriesId))
                        .findFirst()
                        .get()
                        .getAmount()
        );
        assertEquals(
                new BigDecimal("370.85"),
                fetched.getAlertStates().stream()
                        .filter(x -> x.getCategoryId().equals(this.categoryUtilitesId))
                        .findFirst()
                        .get()
                        .getAmount()
        );
        assertEquals(
                new BigDecimal("50.50"),
                fetched.getAlertStates().stream()
                        .filter(x -> x.getCategoryId().equals(this.categoryGroceriesId))
                        .findFirst()
                        .get()
                        .getAmount()
        );
    }

    @Test
    public void createThenFetchAlertWithRecurrenceSuccessIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        AlertConditionCreation conditionCreation = AlertConditionCreation.builder()
                .categoryId(this.listOfCategoriesThatAreNotDefault.get(0).getCategoryId())
                .ruleType(RuleType.BETWEEN)
                .limitValueLowOrEqual(new BigDecimal("5.00"))
                .limitValueHigh(new BigDecimal("150.00"))
                .build();

        AlertRecurrenceRule alertRecurrenceRule = AlertRecurrenceRule.builder()
                .ruleType(RecurrenceRuleType.EVERY_WEEK)
                .recurrenceNum(2)
                .numberOfOccurrences(2)
                .build();

        AlertCreate alertCreate = AlertCreate.builder()
                .name("Entertainment Budget Alert")
                .description("Alert me when entertainment spending is between the set limits.")
                .dateFrom(LocalDate.now().plusDays(1))
                .dateTo(LocalDate.now().plusDays(6))
                .filtersToCreate(List.of(conditionCreation))
                .alertRecurrenceRule(alertRecurrenceRule)
                .build();

        HttpEntity<AlertCreate> entityPost = new HttpEntity<>(alertCreate, headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> res = restTemplate.exchange(
                baseUrl + "/alerts/account/" + this.accountId + "/alert/create",
                HttpMethod.POST, entityPost, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse createResponse = res.getBody();
        assertNotNull(createResponse);

        // when recurrence is used, several alerts are created and no single alertId is returned, we just have number of created alerts inside the msg
        assertNull(createResponse.getId());
        assertTrue(createResponse.getMsg().contains("3"));

        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<List<AlertInformation>> resGet = restTemplate.exchange(
                baseUrl + "/alerts/account/" + this.accountId + "/fetch",
                HttpMethod.GET, entityGet, new ParameterizedTypeReference<List<AlertInformation>>() {});

        List<AlertInformation> fetched = resGet.getBody();

        assertNotNull(fetched);
        assertEquals(3, fetched.size());

        AlertInformation original = fetched.stream()
                .filter(a -> a.getName().equals(alertCreate.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(original);
        assertEquals(alertCreate.getDateFrom(), original.getDateFrom());
        assertEquals(alertCreate.getDateTo(), original.getDateTo());

        long iterationCount = fetched.stream()
                .filter(a -> a.getName().startsWith(alertCreate.getName() + " iteration_"))
                .count();
        assertEquals(2, iterationCount);

        fetched.forEach(alert -> {
            assertEquals(1, alert.getFilters().size());
            assertEquals(conditionCreation.getCategoryId(), alert.getFilters().get(0).getCategoryId());
        });
    }



    @Test
    public void createThenUpdateAlertIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        // create the alert we're about to update
        AlertConditionCreation conditionCreation = AlertConditionCreation.builder()
                .categoryId(this.categoryGroceriesId)
                .ruleType(RuleType.GREATER_OR_EQUAL)
                .limitValueHigh(new BigDecimal("50.00"))
                .build();

        AlertCreate alertCreate = AlertCreate.builder()
                .name("Groceries Budget Alert")
                .description("Alert me when groceries spending breaches limit.")
                .dateFrom(LocalDate.now().minusDays(3))
                .dateTo(LocalDate.now().plusDays(30))
                .filtersToCreate(List.of(conditionCreation))
                .build();

        HttpEntity<AlertCreate> entityPost = new HttpEntity<>(alertCreate, headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> createRes = restTemplate.exchange(
                baseUrl + "/alerts/account/" + this.accountId + "/alert/create",
                HttpMethod.POST, entityPost, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse createResponse = createRes.getBody();
        assertNotNull(createResponse);
        UUID alertId = createResponse.getId();
        assertNotNull(alertId);

        // now update the alert: new name/description and swap the filters to target utilities instead
        AlertConditionCreation updatedCondition = AlertConditionCreation.builder()
                .categoryId(this.categoryUtilitesId)
                .ruleType(RuleType.GREATER_OR_EQUAL)
                .limitValueHigh(new BigDecimal("300.00"))
                .build();

        AlertUpdate alertUpdate = AlertUpdate.builder()
                .name("Groceries Budget Alert Updated")
                .description("Alert me when utilities spending breaches limit instead.")
                .filtersToCreate(List.of(updatedCondition))
                .build();

        HttpEntity<AlertUpdate> entityPatch = new HttpEntity<>(alertUpdate, headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> updateRes = restTemplate.exchange(
                baseUrl + "/alerts/alert/" + alertId + "/update",
                HttpMethod.PATCH, entityPatch, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse updateResponse = updateRes.getBody();
        assertNotNull(updateResponse);
        assertEquals(alertId, updateResponse.getId());
        assertTrue(updateResponse.getMsg().toLowerCase().contains("updated"));

        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<AlertInformation> resGet = restTemplate.exchange(
                baseUrl + "/alerts/alert/" + alertId + "/fetch",
                HttpMethod.GET, entityGet, AlertInformation.class);

        AlertInformation fetched = resGet.getBody();

        assertNotNull(fetched);
        assertEquals(alertId, fetched.getAlertId());
        assertEquals(alertUpdate.getName(), fetched.getName());
        assertEquals(alertUpdate.getDescription(), fetched.getDescription());

        assertEquals(1, fetched.getFilters().size());
        assertEquals(updatedCondition.getCategoryId(), fetched.getFilters().get(0).getCategoryId());
        assertEquals(updatedCondition.getRuleType(), fetched.getFilters().get(0).getRuleType());
    }

    @Test
    public void createThenDeleteAlertIT() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = authHeaders();

        AlertConditionCreation conditionCreation = AlertConditionCreation.builder()
                .categoryId(this.categoryGroceriesId)
                .ruleType(RuleType.GREATER_OR_EQUAL)
                .limitValueHigh(new BigDecimal("50.00"))
                .build();

        AlertCreate alertCreate = AlertCreate.builder()
                .name("Groceries Budget Alert To Delete")
                .description("Alert that is going to be deleted in this test.")
                .dateFrom(LocalDate.now().minusDays(3))
                .dateTo(LocalDate.now().plusDays(30))
                .filtersToCreate(List.of(conditionCreation))
                .build();

        HttpEntity<AlertCreate> entityPost = new HttpEntity<>(alertCreate, headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> createRes = restTemplate.exchange(
                baseUrl + "/alerts/account/" + this.accountId + "/alert/create",
                HttpMethod.POST, entityPost, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse createResponse = createRes.getBody();
        assertNotNull(createResponse);
        UUID alertId = createResponse.getId();
        assertNotNull(alertId);

        HttpEntity<String> entityDelete = new HttpEntity<>(headers);
        ResponseEntity<AlertCreateUpdateDeleteResponse> deleteRes = restTemplate.exchange(
                baseUrl + "/alerts/alert/" + alertId + "/delete",
                HttpMethod.DELETE, entityDelete, AlertCreateUpdateDeleteResponse.class);

        AlertCreateUpdateDeleteResponse deleteResponse = deleteRes.getBody();
        assertNotNull(deleteResponse);
        assertEquals(alertId, deleteResponse.getId());
        assertTrue(deleteResponse.getMsg().toLowerCase().contains("deleted"));

        // fetching the deleted alert should now fail
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        ResponseEntity<String> resGet = restTemplate.exchange(
                baseUrl + "/alerts/alert/" + alertId + "/fetch",
                HttpMethod.GET, entityGet, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, resGet.getStatusCode());
    }


}
