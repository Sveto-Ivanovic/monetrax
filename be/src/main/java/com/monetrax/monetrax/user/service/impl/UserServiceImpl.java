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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        return userMapper.toUserInformation(user);
    }

    public UserInformation createUser(UserCreation user){
        if(userRepository.existsUserEmail(user.getUserEmail()))
            throw new EmailAlreadyExistsException("Cannot create user as email already exists.");

        String hashedPass = hashString(user.getPassword());
        UserEntity toCreateUser = userMapper.fromUserCreationToUserEntity(user, hashedPass);
        UserEntity userRes = userRepository.save(toCreateUser);
        return userMapper.toUserInformation(userRes);
    }

    public UserInformation updateUser(UserUpdate userUpdate, UUID userId){

        if (userUpdate.getDateOfBirth() == null
                && userUpdate.getName() == null
                && userUpdate.getSurname() == null
                && userUpdate.getUserName() == null
                && userUpdate.getUserEmail() == null) {
            throw new NoFieldToUpdateUserExistsException("Nothing to update user with.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));

        Optional.ofNullable(userUpdate.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userUpdate.getName()).ifPresent(user::setName);
        Optional.ofNullable(userUpdate.getSurname()).ifPresent(user::setSurname);
        Optional.ofNullable(userUpdate.getUserName()).ifPresent(user::setUserName);

        if(userUpdate.getUserEmail() != null && userRepository.existsUserEmail(userUpdate.getUserEmail()))
            throw new EmailAlreadyExistsException("Cannot update user with present email as the email already exists.");

        Optional.ofNullable(userUpdate.getUserEmail()).ifPresent(user::setUserEmail);

        UserEntity  userEntity = userRepository.save(user);

        return userMapper.toUserInformation(userEntity);
    }

    public UserSuccessfulPasswordUpdate updatePassword(String newPassword, String oldPassword, UUID userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        String hashedPasswordFromDB = user.getPasswordHash();
        if(!passwordEncoder.matches(oldPassword, hashedPasswordFromDB)){
            throw new PasswordMismatchException("The provided old password is not equal to the one provided in database.");
        }
        if(passwordEncoder.matches(newPassword, hashedPasswordFromDB)){
            throw new PasswordMismatchException("The new password must not be equal to the old one.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return new UserSuccessfulPasswordUpdate(true);
    }

}
