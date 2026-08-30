package com.monetrax.monetrax.user.controller;

import com.monetrax.monetrax.user.dto.UserCreation;
import com.monetrax.monetrax.user.dto.UserInformation;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me/{user_id}")
    public ResponseEntity<UserInformation> getUser(@PathVariable UUID user_id){
        UserEntity user = userService.fetchUserById(user_id);
        return ResponseEntity.ok(userMapper.toUserInformation(user));
    }

    @PostMapping("/create")
    public ResponseEntity<UserInformation> createUser(@Valid @RequestBody UserCreation req){
        String encoded_password = passwordEncoder.encode(req.getPassword());
        UserEntity toCreateUser = userMapper.fromUserCreationToUserEntity(req, encoded_password);
        UserEntity user = userService.createUser(toCreateUser);
        return ResponseEntity.ok(userMapper.toUserInformation(user));
    }

}
