package com.monetrax.monetrax.user.controller;

import com.monetrax.monetrax.config.SecurityConfig;
import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({UserMapper.class, SecurityConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserEntity userEntityTest;

    {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        userEntityTest =   UserEntity.builder()
                .userId(UUID.randomUUID())
                .userEmail("test.user@example.com")
                .userName("testuser")
                .name("Test")
                .surname("User")
                .role("USER")
                .createdAt(now)
                .updatedAt(now)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .passwordHash("$2a$10$testPasswordHash")
                .hasFinishedOnboarding(true)
                .hasVerifiedEmail(true)
                .lastLoggedIn(now)
                .additionalInfo("Test user")
                .build();
    }

    @Test
    public void getUserSuccessTest() throws Exception{
        UUID searchUUID = userEntityTest.getUserId();
        String path = "/user/me/"+searchUUID.toString();
        when(userService.fetchUserById(searchUUID)).thenReturn(userEntityTest);

        UserInformation userInformation = userMapper.toUserInformation(userEntityTest);
        String userInformationJsonString = objectMapper.writeValueAsString(userInformation);
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().json(userInformationJsonString));

        verify(userService).fetchUserById(searchUUID);
    }

    @Test
    public void getUserMalformedPathTest() throws Exception{
        UUID searchUUID = userEntityTest.getUserId();
        String path = "/user/me/test123";
        when(userService.fetchUserById(searchUUID)).thenReturn(userEntityTest);

        String expectedResponse = """
                {"errors":[{"clue":"param:user_id","message":"Method parameter 'user_id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: test123"}],"status":400}""";
        mockMvc.perform(get(path))
                .andExpect(content().json(expectedResponse))
                .andExpect(status().isBadRequest());
        verify(userService, never()).fetchUserById(any());
    }

    @Test
    public void getNonExistentUserFailureTest() throws Exception{
        UUID searchUUID = userEntityTest.getUserId();
        String path = "/user/me/"+searchUUID.toString();
        when(userService.fetchUserById(searchUUID)).thenThrow(new NoSuchUserExistsException("No user with id: "+ userEntityTest.getUserId()));

        String expectedResponse = """
                        {"errors":[{"clue":"param:user_id", "message":"No user with id: %s"}],"status":404}""".formatted(userEntityTest.getUserId());

        mockMvc.perform(get(path))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedResponse));

        verify(userService).fetchUserById(searchUUID);
    }

    @Test
    public void createUserSuccessTest() throws Exception{
        String path = "/user/create";
        UserCreation userCreationTest = UserCreation.builder()
                .userEmail(userEntityTest.getUserEmail())
                .userName(userEntityTest.getUserName())
                .name(userEntityTest.getName())
                .surname(userEntityTest.getSurname())
                .dateOfBirth(userEntityTest.getDateOfBirth())
                .password("TestPassword123!")
                .build();

        String encoded_password = passwordEncoder.encode(userCreationTest.getPassword());

        UserEntity userEntity = userMapper.fromUserCreationToUserEntity(userCreationTest, encoded_password);
        UserInformation userInformation =userMapper.toUserInformation(userEntity);

        String userInformationJSONString = objectMapper.writeValueAsString(userInformation);
        String userCreationJSONString = objectMapper.writeValueAsString(userCreationTest);

        when(userService.createUser(any(UserEntity.class))).thenReturn(userEntity);

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(userCreationJSONString))
                .andExpect(content().json(userInformationJSONString))
                .andExpect(status().isOk());

        verify(userService).createUser(any(UserEntity.class));
    }

    @Test
    public void createUserDuplicateEmailFailureTest() throws Exception{
        String path = "/user/create";
        UserCreation userCreationTest = UserCreation.builder()
                .userEmail(userEntityTest.getUserEmail())
                .userName(userEntityTest.getUserName())
                .name(userEntityTest.getName())
                .surname(userEntityTest.getSurname())
                .dateOfBirth(userEntityTest.getDateOfBirth())
                .password("TestPassword123!")
                .build();

        String userCreationJSONString = objectMapper.writeValueAsString(userCreationTest);

        when(userService.createUser(any(UserEntity.class))).thenThrow(new EmailAlreadyExistsException("Cannot create user as email already exists."));

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(userCreationJSONString))
                .andExpect(content().json("""
                        {"errors": [{"message":"Cannot create user as email already exists.", "clue":"userEmail"}],"status":403}"""))
                .andExpect(status().isForbidden());

        verify(userService).createUser(any(UserEntity.class));
    }


    @Test
    public void createUserMultiValidationFailureTest() throws Exception{
        String path = "/user/create";
        UserCreation userCreationTest = UserCreation.builder()
                .userEmail("incorr.ect@emailformat")
                .userName("NA")
                .name("NA")
                .surname("LP")
                .dateOfBirth(LocalDate.of(2444, 1, 1))
                .password("TestPassword123")
                .build();

        String encoded_password = passwordEncoder.encode(userCreationTest.getPassword());

        UserEntity userEntity = userMapper.fromUserCreationToUserEntity(userCreationTest, encoded_password);

        String userCreationJSONString = objectMapper.writeValueAsString(userCreationTest);

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(userCreationJSONString))
                .andExpect(content().json("""
                        {"status":400,"errors":[{"clue":"dateOfBirth","message":"The birth date must be in the past."},{"clue":"dateOfBirth","message":"User has to be at least 18 years old."},{"clue":"userEmail","message":"Invalid email format."},{"clue":"password","message":"Invalid Password format, password must contain at least 1 uppercase, lowercase, number and symbol."}]}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserMissingRequestParameterFailureTest() throws Exception{
        String path = "/user/create";
        UserCreation userCreationTest = UserCreation.builder()
                .userEmail("correct@email.com")
                .userName("NA")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .password("TestPassword123.")
                .build();

        String userCreationJSONString = objectMapper.writeValueAsString(userCreationTest);

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(userCreationJSONString))
                .andExpect(content().json("""
                        {"errors":[{"message":"surname must be present.","clue":"surname"},{"message":"name must be present.","clue":"name"}],"status":400}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserWrongParameterTypeConversionFailureTest() throws Exception{
        String path = "/user/create";

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("""
                        {"userEmail":"correct@email.com","userName":"Username","surname":"heter", "name":55, "dateOfBirth":"hello","password":"TestPassword123."}"""))
                .andExpect(content().json("""
                        {"errors":[{"clue":"InvalidParamType","message":"JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \\"hello\\": Failed to deserialize `java.time.LocalDate` (with format 'Value(Year,4,10,EXCEEDS_PAD)'-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)'): (java.time.format.DateTimeParseException) Text 'hello' could not be parsed at index 0"}],"status":400}"""))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
