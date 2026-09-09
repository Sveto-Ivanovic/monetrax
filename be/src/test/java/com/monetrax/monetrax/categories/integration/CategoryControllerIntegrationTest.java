package com.monetrax.monetrax.categories.integration;

import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;

import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import org.junit.jupiter.api.*;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        List<CategoryEntity> allCategories = categoryRepository.fetchUsersAndDefaultCategories(true, this.userId);
        allCategories.forEach(element->{
            if(!element.isDefaultCategory())
                categoryRepository.delete(element);
        });
    }

    private CategoryCreate categoryCreate =  CategoryCreate.builder()
            .categoryType(CategoryKind.EXPENSE)
            .name("Voli Store Purchase")
            .description("Category for purchasing everyday supplies in Voli store.")
            .build();

    @Test
    public void createThenFetchUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<String> entityGet = new HttpEntity<>(headers);
        HttpEntity<CategoryCreate> entityPost = new HttpEntity<>(categoryCreate, headers);

        ResponseEntity<CategoryInformation> res = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost, CategoryInformation.class);

        CategoryInformation resCategoryInformation = res.getBody();

        ResponseEntity<CategoryInformation> resGet = restTemplate.exchange(
                baseUrl + "/categories/category/"+resCategoryInformation.getCategoryId(), HttpMethod.GET, entityGet, CategoryInformation.class);

        assertEquals(resGet.getBody(), resCategoryInformation);
    }

    @Test
    public void createUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<CategoryCreate> entityPost = new HttpEntity<>(categoryCreate, headers);

        ResponseEntity<CategoryInformation> res = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost, CategoryInformation.class);

        CategoryInformation resCategoryInformation = res.getBody();

        assertEquals(categoryCreate.getCategoryType(), resCategoryInformation.getCategoryType());
        assertEquals(categoryCreate.getName(), resCategoryInformation.getName());
        assertEquals(categoryCreate.getDescription(), resCategoryInformation.getDescription());
    }

    @Test
    public void createThenDeleteUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<String> entityGetOrDelete = new HttpEntity<>(headers);
        HttpEntity<CategoryCreate> entityPost = new HttpEntity<>(categoryCreate, headers);

        ResponseEntity<CategoryInformation> res = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost, CategoryInformation.class);

        CategoryInformation resCategoryInformation = res.getBody();


        restTemplate.exchange(
                baseUrl + "/categories/category/%s/delete".formatted(resCategoryInformation.getCategoryId()), HttpMethod.DELETE, entityGetOrDelete, CategoryDeletionSuccess.class);

        ResponseEntity<ErrorResponse> resGet = restTemplate.exchange(
                baseUrl + "/categories/category/"+resCategoryInformation.getCategoryId(), HttpMethod.GET, entityGetOrDelete, ErrorResponse.class);

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("No category with id: "+ resCategoryInformation.getCategoryId(),"category_idORuser_id");
        ErrorResponse expectedResponse = new ErrorResponse(400,expectedErrors);
        assertEquals(expectedResponse,  resGet.getBody());
    }


    @Test
    public void createThenUpdateUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<String> entityGetOrDelete = new HttpEntity<>(headers);
        HttpEntity<CategoryCreate> entityPost = new HttpEntity<>(categoryCreate, headers);

        ResponseEntity<CategoryInformation> res = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost, CategoryInformation.class);

        CategoryInformation resCategoryInformation = res.getBody();

        CategoryUpdate categoryUpdateTest =  CategoryUpdate.builder()
                .name("Idea Store Purchase")
                .build();

        HttpEntity<CategoryUpdate> entityPatch = new HttpEntity<>(categoryUpdateTest, headers);

        ResponseEntity<CategoryInformation> resUpdate = restTemplate.exchange(
                baseUrl + "/categories/category/%s/update".formatted(resCategoryInformation.getCategoryId()), HttpMethod.PATCH, entityPatch, CategoryInformation.class);

        ResponseEntity<CategoryInformation> resGet = restTemplate.exchange(
                baseUrl + "/categories/category/"+resCategoryInformation.getCategoryId(), HttpMethod.GET, entityGetOrDelete, CategoryInformation.class);

        assertNotEquals(resGet.getBody(), resCategoryInformation);
        assertEquals("Idea Store Purchase", resGet.getBody().getName());
    }



    @Test
    public void fetchAllCategoriesIT(){
        String baseUrl = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+this.authToken);
        HttpEntity<String> entityGetOrDelete = new HttpEntity<>(headers);
        HttpEntity<CategoryCreate> entityPost = new HttpEntity<>(categoryCreate, headers);

        ResponseEntity<FetchAllCategoriesResponse> baselineRes = restTemplate.exchange(
                baseUrl + "/categories/all", HttpMethod.GET, entityGetOrDelete, FetchAllCategoriesResponse.class);
        int baselineCount = baselineRes.getBody().getCategories().size();

        ResponseEntity<CategoryInformation> res = restTemplate.exchange(
                baseUrl + "/categories/create", HttpMethod.POST, entityPost, CategoryInformation.class);

        CategoryInformation resCategoryInformation = res.getBody();


        ResponseEntity<FetchAllCategoriesResponse> resGet = restTemplate.exchange(
                baseUrl + "/categories/all", HttpMethod.GET, entityGetOrDelete, FetchAllCategoriesResponse.class);

        assertEquals(baselineCount+1, resGet.getBody().getCategories().size());
        assertTrue(resGet.getBody().getCategories().contains(resCategoryInformation));

    }


}
