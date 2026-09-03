package com.monetrax.monetrax.user.controller;

import com.monetrax.monetrax.user.dto.*;
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

    @GetMapping("/me/{user_id}")
    public ResponseEntity<UserInformation> getUser(@PathVariable UUID user_id){
        return ResponseEntity.ok(userService.fetchUserById(user_id));
    }

    @PostMapping("/create")
    public ResponseEntity<UserInformation> createUser(@Valid @RequestBody UserCreation req){
        UserInformation userInfo = userService.createUser(req);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/update/{user_id}")
    public ResponseEntity<UserInformation> updateUser(@PathVariable UUID user_id, @Valid @RequestBody UserUpdate req){
        return ResponseEntity.ok(userService.updateUser(req, user_id));
    }

    @PatchMapping("/update/{user_id}/password")
    public ResponseEntity<UserSuccessfulPasswordUpdate> updateUserPassword(@PathVariable UUID user_id, @Valid @RequestBody UserUpdatePassword req){
        return ResponseEntity.ok(userService.updatePassword(req.getNewPassword(), req.getOldPassword(), user_id));
    }


}
