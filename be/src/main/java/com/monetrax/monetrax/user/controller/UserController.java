package com.monetrax.monetrax.user.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.user.dto.*;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInformation> getUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(userService.fetchUserById(UUID.fromString(customUserDetails.getUserId())));
    }

    @PostMapping("/create")
    public ResponseEntity<UserInformation> createUser(@Valid @RequestBody UserCreation req){
        UserInformation userInfo = userService.createUser(req);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserInformation> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody UserUpdate req){
        return ResponseEntity.ok(userService.updateUser(req, UUID.fromString(customUserDetails.getUserId())));
    }

    @PatchMapping("/update/password")
    public ResponseEntity<UserSuccessfulPasswordUpdate> updateUserPassword(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody UserUpdatePassword req){
        return ResponseEntity.ok(userService.updatePassword(req.getNewPassword(), req.getOldPassword(), UUID.fromString(customUserDetails.getUserId())));
    }


}
