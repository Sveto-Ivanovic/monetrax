package com.monetrax.monetrax.user.mapper;

import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class UserMapper {
    public UserInformation toUserInformation(UserEntity user){
        return UserInformation
                .builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .name(user.getName())
                .surname(user.getSurname())
                .role(user.getRole())
                .dateOfBirth(user.getDateOfBirth())
                .hasFinishedOnboarding(user.isHasFinishedOnboarding())
                .hasVerifiedEmail(user.isHasVerifiedEmail())
                .additionalInfo(user.getAdditionalInfo())
                .build();

    }

    public UserEntity fromUserCreationToUserEntity(UserCreation user, String passwordHash){
        return UserEntity.builder()
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .name(user.getName())
                .surname(user.getSurname())
                .role("user")
                .dateOfBirth(user.getDateOfBirth())
                .hasFinishedOnboarding(false)
                .hasVerifiedEmail(false)
                .additionalInfo("{}")
                .passwordHash(passwordHash)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

}
