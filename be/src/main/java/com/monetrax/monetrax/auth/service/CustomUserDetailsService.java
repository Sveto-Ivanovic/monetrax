package com.monetrax.monetrax.auth.service;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws NoSuchUserExistsException {

        Optional<UserEntity> userEntityOptional = userRepository.findUserByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(()->{
            return new NoSuchUserExistsException("User with such email doesn't exist.");
        });

        return new CustomUserDetails(userEntity);
    }

}
