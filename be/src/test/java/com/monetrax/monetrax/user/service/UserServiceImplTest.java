package com.monetrax.monetrax.user.service;

import com.monetrax.monetrax.common.exception.ErrorResponse;
import com.monetrax.monetrax.user.dto.*;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoFieldToUpdateUserExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.exception.PasswordMismatchException;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.repository.UserRepository;
import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import com.monetrax.monetrax.user.dto.UserSuccessfulPasswordUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserEntity userEntityTest;
    private final UserInformation userInformationTest;
    private final UserCreation userCreationTest;


    {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        userEntityTest = UserEntity.builder()
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

        userInformationTest = UserInformation.builder()
                .userId(UUID.randomUUID())
                .userEmail("test.user@example.com")
                .userName("testuser")
                .name("Test")
                .surname("User")
                .role("USER")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .hasFinishedOnboarding(true)
                .hasVerifiedEmail(true)
                .additionalInfo("Test user")
                .build();

        userCreationTest = UserCreation.builder()
                .userEmail("test.user@example.com")
                .userName("testuser")
                .name("Test")
                .surname("User")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .password("testPassword123!")
                .build();

    }

    @Test
    public void fetchUserByIdSuccessTest(){
        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityTest));
        when(userMapper.toUserInformation(userEntityTest)).thenReturn(userInformationTest);

        UserInformation resUser = userService.fetchUserById(userEntityTest.getUserId());

        assertNotNull(resUser);
        assertEquals(userInformationTest, resUser);
        verify(userRepository).findById(userEntityTest.getUserId());
        verify(userMapper).toUserInformation(userEntityTest);
    }

    @Test
    public void fetchUserByIdNoSuchUserFailureTest(){
        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.empty());
        NoSuchUserExistsException exc = assertThrows(NoSuchUserExistsException.class, ()->{
            userService.fetchUserById(userEntityTest.getUserId());
        });
        assertTrue(exc.msg.contains("No user with id: "+ userEntityTest.getUserId()));

        verify(userRepository).findById(userEntityTest.getUserId());
    }

    @Test
    public void createUserSuccessTest(){
        when(userRepository.save(userEntityTest)).thenReturn(userEntityTest);
        when(userMapper.fromUserCreationToUserEntity(userCreationTest, "$2a$10$testPasswordHash")).thenReturn(userEntityTest);
        when(userMapper.toUserInformation(userEntityTest)).thenReturn(userInformationTest);
        when(passwordEncoder.encode("testPassword123!")).thenReturn("$2a$10$testPasswordHash");
        UserInformation res = userService.createUser(userCreationTest);

        assertEquals(res, userInformationTest);
        verify(userRepository).save(userEntityTest);
        verify(passwordEncoder).encode("testPassword123!");
        verify(userMapper).fromUserCreationToUserEntity(userCreationTest, "$2a$10$testPasswordHash");
        verify(userMapper).toUserInformation(userEntityTest);
    }

    @Test
    public void createUserEmailAlreadyExistsFailureTest(){
        when(userRepository.existsUserEmail(userEntityTest.getUserEmail())).thenReturn(true);
        EmailAlreadyExistsException exc = assertThrows(EmailAlreadyExistsException.class, ()->{
            userService.createUser(userCreationTest);
        });

        assertEquals("Cannot create user as email already exists.",exc.msg);
        verify(userRepository).existsUserEmail(userEntityTest.getUserEmail());
    }

    @Test
    public void updateUserSuccessTest(){
        UserUpdate userUpdate = UserUpdate.builder()
                .userEmail("updated.user@example.com")
                .userName("updateduser")
                .name("Updated")
                .surname("Person")
                .dateOfBirth(LocalDate.of(1992, 5, 15))
                .build();


        UserEntity updatedUserEntity = UserEntity.builder()
                .userId(userEntityTest.getUserId())
                .userEmail(userUpdate.getUserEmail())
                .userName(userUpdate.getUserName())
                .name(userUpdate.getName())
                .surname(userUpdate.getSurname())
                .role(userEntityTest.getRole())
                .createdAt(userEntityTest.getCreatedAt())
                .updatedAt(userEntityTest.getUpdatedAt())
                .dateOfBirth(userUpdate.getDateOfBirth())
                .passwordHash(userEntityTest.getPasswordHash())
                .hasFinishedOnboarding(userEntityTest.isHasFinishedOnboarding())
                .hasVerifiedEmail(userEntityTest.isHasVerifiedEmail())
                .lastLoggedIn(userEntityTest.getLastLoggedIn())
                .additionalInfo(userEntityTest.getAdditionalInfo())
                .build();

        when(userRepository.existsUserEmail(userUpdate.getUserEmail())).thenReturn(false);
        when(userMapper.toUserInformation(userEntityTest)).thenReturn(userInformationTest);
        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityTest));
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);

        UserInformation res = userService.updateUser(userUpdate, userEntityTest.getUserId());

        assertNotNull(res);
        assertEquals(userInformationTest, res);

        verify(userRepository).existsUserEmail(userEntityTest.getUserEmail());
        verify(userRepository).findById(userEntityTest.getUserId());
        verify(userRepository).save(updatedUserEntity);
    }


    @Test
    public void updateUserEmailAlreadyExistsFailureTest() {
        UserUpdate userUpdate = UserUpdate.builder()
                .userEmail("updated.user@example.com")
                .userName("updateduser")
                .name("Updated")
                .surname("Person")
                .dateOfBirth(LocalDate.of(1992, 5, 15))
                .build();

        when(userRepository.existsUserEmail(userUpdate.getUserEmail())).thenReturn(true);
        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityTest));

        assertThrows(EmailAlreadyExistsException.class, ()->{
            userService.updateUser(userUpdate, userEntityTest.getUserId());
        });
    }

    @Test
    public void updateUserThatUserDoesNotExistFailureTest() {
        UserUpdate userUpdate = UserUpdate.builder()
                .userEmail("updated.user@example.com")
                .userName("updateduser")
                .name("Updated")
                .surname("Person")
                .dateOfBirth(LocalDate.of(1992, 5, 15))
                .build();

        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserExistsException.class, ()->{
            userService.updateUser(userUpdate, userEntityTest.getUserId());
        });
    }

    @Test
    public void updateUserWithNoNewValuesToUpdateFailureTest() {
        UserUpdate userUpdate = UserUpdate.builder()
                .build();

        assertThrows(NoFieldToUpdateUserExistsException.class, ()->{
            userService.updateUser(userUpdate, userEntityTest.getUserId());
        });
    }

    @Test void updatePasswordSuccessTest(){
        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .newPassword("testPassword123456!")
                .oldPassword("testPassword123!")
                .build();

        UserEntity userEntityFetched = userEntityTest.toBuilder().build();

        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityFetched));
        when(passwordEncoder.matches(userUpdatePassword.getOldPassword(), userEntityTest.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches(userUpdatePassword.getNewPassword(), userEntityTest.getPasswordHash())).thenReturn(false);
        when(passwordEncoder.encode(userUpdatePassword.getNewPassword())).thenReturn("$2a$10$testPasswordHash2");
        UserEntity copyOfUser = userEntityTest.toBuilder()
                        .passwordHash("$2a$10$testPasswordHash2")
                        .build();
        when(userRepository.save(copyOfUser)).thenReturn(copyOfUser);

        UserSuccessfulPasswordUpdate expectedResponse = new UserSuccessfulPasswordUpdate(true);
        UserSuccessfulPasswordUpdate userUpdatePasswordResponse = userService.updatePassword(userUpdatePassword.getNewPassword(),userUpdatePassword.getOldPassword(), userEntityTest.getUserId());

        assertNotNull(userUpdatePasswordResponse);
        assertEquals(expectedResponse, userUpdatePasswordResponse);
        assertTrue(userUpdatePasswordResponse.isSuccessfulPasswordUpdate());

        verify(userRepository).findById(userEntityTest.getUserId());
        verify(passwordEncoder).matches(userUpdatePassword.getOldPassword(), userEntityTest.getPasswordHash());
        verify(passwordEncoder).matches(userUpdatePassword.getNewPassword(), userEntityTest.getPasswordHash());
        verify(passwordEncoder).encode(userUpdatePassword.getNewPassword());
        verify(userRepository).save(copyOfUser);
    }

    @Test void updatePasswordNewOldPasswordMatchFailureTest(){
        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .newPassword("testPassword123456!")
                .oldPassword("testPassword123!")
                .build();

        UserEntity userEntityFetched = userEntityTest.toBuilder().build();

        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityFetched));
        when(passwordEncoder.matches(userUpdatePassword.getOldPassword(), userEntityTest.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches(userUpdatePassword.getNewPassword(), userEntityTest.getPasswordHash())).thenReturn(true);

        assertThrows(PasswordMismatchException.class, ()->{
            userService.updatePassword(userUpdatePassword.getNewPassword(),userUpdatePassword.getOldPassword(), userEntityTest.getUserId());
        });
    }

    @Test void updatePasswordIncorrectCurrentFailureTest(){
        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .newPassword("testPassword123456!")
                .oldPassword("testPassword123!")
                .build();

        UserEntity userEntityFetched = userEntityTest.toBuilder().build();

        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.of(userEntityFetched));
        when(passwordEncoder.matches(userUpdatePassword.getOldPassword(), userEntityTest.getPasswordHash())).thenReturn(false);

        assertThrows(PasswordMismatchException.class, ()->{
            userService.updatePassword(userUpdatePassword.getNewPassword(),userUpdatePassword.getOldPassword(), userEntityTest.getUserId());
        });
    }

    @Test void updatePasswordForNonExistentUserFailureTest(){
        UserUpdatePassword userUpdatePassword = UserUpdatePassword.builder()
                .newPassword("testPassword123456!")
                .oldPassword("testPassword123!")
                .build();

        UserEntity userEntityFetched = userEntityTest.toBuilder().build();

        when(userRepository.findById(userEntityTest.getUserId())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserExistsException.class, ()->{
            userService.updatePassword(userUpdatePassword.getNewPassword(),userUpdatePassword.getOldPassword(), userEntityTest.getUserId());
        });
    }

    }
