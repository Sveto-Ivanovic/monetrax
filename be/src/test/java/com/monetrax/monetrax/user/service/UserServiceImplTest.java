package com.monetrax.monetrax.user.service;

import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.repository.UserRepository;
import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void fetchUserByIdSuccessTest(){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        UserEntity userEntity=   UserEntity.builder()
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


        when(userRepository.findById(userEntity.getUserId())).thenReturn(Optional.of(userEntity));
        UserEntity resUser = userService.fetchUserById(userEntity.getUserId());

        assertNotNull(resUser);
        assertEquals(userEntity, resUser);
        verify(userRepository).findById(userEntity.getUserId());
    }

}
