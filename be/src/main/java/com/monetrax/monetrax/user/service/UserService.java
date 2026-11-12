package com.monetrax.monetrax.user.service;

import com.monetrax.monetrax.user.dto.*;
import com.monetrax.monetrax.user.entity.UserEntity;
import java.util.UUID;

public interface UserService {
    UserInformationDetailed fetchUserById(UUID userId);
    UserInformation createUser(UserCreation user);
    UserInformation updateUser(UserUpdate user, UUID userId);
    UserSuccessfulPasswordUpdate updatePassword(String newHashedPassword, String oldHashedPassword, UUID userId);
    String hashString(String val);
}
