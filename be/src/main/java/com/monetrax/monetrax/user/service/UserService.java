package com.monetrax.monetrax.user.service;

import com.monetrax.monetrax.user.entity.UserEntity;
import java.util.UUID;

public interface UserService {
    UserEntity fetchUserById(UUID userId);
    UserEntity createUser(UserEntity user);
}
