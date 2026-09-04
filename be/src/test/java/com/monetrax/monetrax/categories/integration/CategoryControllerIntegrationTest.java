package com.monetrax.monetrax.categories.integration;

import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.dto.FetchAllCategoriesResponse;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.common.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanChanges(){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        List<CategoryEntity> allCategories = categoryRepository.fetchUsersAndDefaultCategories(true, userId);
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

    private String getBaseURL(){
        return "http://localhost:"+port;
    }


    @Test
    public void createThenFetchUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<CategoryInformation> res = restTemplate.postForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/create", categoryCreate, CategoryInformation.class);
        CategoryInformation resCategoryInformation = res.getBody();

        ResponseEntity<CategoryInformation> resGet = restTemplate.getForEntity(
                baseUrl +  "/categories/user/00000000-0000-0000-0000-000000000001/category/"+resCategoryInformation.getCategoryId(), CategoryInformation.class);
        assertEquals(resGet.getBody(), resCategoryInformation);
    }

    @Test
    public void createUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<CategoryInformation> res = restTemplate.postForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/create", categoryCreate, CategoryInformation.class);
        CategoryInformation resCategoryInformation = res.getBody();

        assertEquals(categoryCreate.getCategoryType(), resCategoryInformation.getCategoryType());
        assertEquals(categoryCreate.getName(), resCategoryInformation.getName());
        assertEquals(categoryCreate.getDescription(), resCategoryInformation.getDescription());
    }

    @Test
    public void createThenDeleteUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<CategoryInformation> res = restTemplate.postForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/create", categoryCreate, CategoryInformation.class);
        CategoryInformation resCategoryInformation = res.getBody();

         restTemplate.delete(baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/category/%s/delete".formatted(resCategoryInformation.getCategoryId()));

        ResponseEntity<ErrorResponse> resGet = restTemplate.getForEntity(
                baseUrl +  "/categories/user/00000000-0000-0000-0000-000000000001/category/"+resCategoryInformation.getCategoryId(), ErrorResponse.class);

        var expectedErrors = GlobalExceptionHandler.addCustomErrorToErrorResponse("No category with id: "+ resCategoryInformation.getCategoryId(),"category_idORuser_id");
        ErrorResponse expectedResponse = new ErrorResponse(400,expectedErrors);
        assertEquals(expectedResponse,  resGet.getBody());
    }


    @Test
    public void createThenUpdateUserSuccessIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<CategoryInformation> res = restTemplate.postForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/create", categoryCreate, CategoryInformation.class);
        CategoryInformation resCategoryInformation = res.getBody();

        CategoryCreate categoryCreateTest =  CategoryCreate.builder()
                .categoryType(CategoryKind.INCOME)
                .name("Idea Store Purchase")
                .build();

        CategoryInformation resUpdate = restTemplate.patchForObject(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/category/%s/update".formatted(resCategoryInformation.getCategoryId()), categoryCreateTest, CategoryInformation.class);

        ResponseEntity<CategoryInformation> resGet = restTemplate.getForEntity(
                baseUrl +  "/categories/user/00000000-0000-0000-0000-000000000001/category/"+resCategoryInformation.getCategoryId(), CategoryInformation.class);

        assertNotEquals(resGet.getBody(), resCategoryInformation);
        assertEquals("Idea Store Purchase", resGet.getBody().getName());
        assertEquals(CategoryKind.INCOME, resGet.getBody().getCategoryType());
    }



    @Test
    public void fetchAllCategoriesIT(){
        String baseUrl = "http://localhost:" + port;

        ResponseEntity<FetchAllCategoriesResponse> baselineRes = restTemplate.getForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/all", FetchAllCategoriesResponse.class);
        int baselineCount = baselineRes.getBody().getCategories().size();

        ResponseEntity<CategoryInformation> res = restTemplate.postForEntity(
                baseUrl + "/categories/user/00000000-0000-0000-0000-000000000001/create", categoryCreate, CategoryInformation.class);
        CategoryInformation resCategoryInformation = res.getBody();


        ResponseEntity<FetchAllCategoriesResponse> resGet = restTemplate.getForEntity(
                baseUrl +  "/categories/user/00000000-0000-0000-0000-000000000001/all",FetchAllCategoriesResponse.class);

        assertEquals(baselineCount+1, resGet.getBody().getCategories().size());
        assertTrue(resGet.getBody().getCategories().contains(resCategoryInformation));

    }


}
