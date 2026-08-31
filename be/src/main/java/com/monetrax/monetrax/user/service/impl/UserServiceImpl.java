package com.monetrax.monetrax.user.service.impl;

import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import com.monetrax.monetrax.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public UserEntity fetchUserById(UUID userId){
        return userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
    }

    public UserEntity createUser(UserEntity user){
        if(userRepository.existsUserEmail(user.getUserEmail()))
            throw new EmailAlreadyExistsException("Cannot create user as email already exists.");
        return userRepository.save(user);
    }

}
