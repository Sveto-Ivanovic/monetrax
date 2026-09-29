package com.monetrax.monetrax.user.service.impl;

import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.dto.UserSuccessfulPasswordUpdate;
import com.monetrax.monetrax.user.dto.UserUpdate;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoFieldToUpdateUserExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.exception.PasswordMismatchException;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.repository.UserRepository;
import com.monetrax.monetrax.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public String hashString(String val){
        return passwordEncoder.encode(val);
    }

    public UserInformation fetchUserById(UUID userId){
        log.debug("Fetching user [userId={}]", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User fetch failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });
        log.debug("Fetched user [userId={}]", userId);
        return userMapper.toUserInformation(user);
    }

    public UserInformation createUser(UserCreation user){
        log.info("Creating user");

        if(userRepository.existsUserEmail(user.getUserEmail())) {
            log.warn("User creation rejected, email already exists");
            throw new EmailAlreadyExistsException("Cannot create user as email already exists.");
        }

        String hashedPass = hashString(user.getPassword());
        UserEntity toCreateUser = userMapper.fromUserCreationToUserEntity(user, hashedPass);
        UserEntity userRes = userRepository.save(toCreateUser);
        log.info("User created [userId={}]", userRes.getUserId());
        return userMapper.toUserInformation(userRes);
    }

    public UserInformation updateUser(UserUpdate userUpdate, UUID userId){
        log.info("Updating user [userId={}]", userId);

        if (userUpdate.getDateOfBirth() == null
                && userUpdate.getName() == null
                && userUpdate.getSurname() == null
                && userUpdate.getUserName() == null
                && userUpdate.getUserEmail() == null) {
            log.warn("User update rejected, no fields provided [userId={}]", userId);
            throw new NoFieldToUpdateUserExistsException("Nothing to update user with.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User update failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });

        Optional.ofNullable(userUpdate.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userUpdate.getName()).ifPresent(user::setName);
        Optional.ofNullable(userUpdate.getSurname()).ifPresent(user::setSurname);
        Optional.ofNullable(userUpdate.getUserName()).ifPresent(user::setUserName);
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        if(userUpdate.getUserEmail() != null && userRepository.existsUserEmail(userUpdate.getUserEmail())) {
            log.warn("User update rejected, email already exists [userId={}]", userId);
            throw new EmailAlreadyExistsException("Cannot update user with present email as the email already exists.");
        }

        Optional.ofNullable(userUpdate.getUserEmail()).ifPresent(user::setUserEmail);

        UserEntity  userEntity = userRepository.save(user);
        log.info("User updated [userId={}]", userId);

        return userMapper.toUserInformation(userEntity);
    }

    public UserSuccessfulPasswordUpdate updatePassword(String newPassword, String oldPassword, UUID userId){
        log.info("Updating password [userId={}]", userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Password update failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });
        String hashedPasswordFromDB = user.getPasswordHash();
        if(!passwordEncoder.matches(oldPassword, hashedPasswordFromDB)){
            log.warn("Password update rejected, old password incorrect [userId={}]", userId);
            throw new PasswordMismatchException("The provided old password is not equal to the one provided in database.");
        }
        if(passwordEncoder.matches(newPassword, hashedPasswordFromDB)){
            log.warn("Password update rejected, new password same as old [userId={}]", userId);
            throw new PasswordMismatchException("The new password must not be equal to the old one.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
        log.info("Password updated [userId={}]", userId);
        return new UserSuccessfulPasswordUpdate(true);
    }

}