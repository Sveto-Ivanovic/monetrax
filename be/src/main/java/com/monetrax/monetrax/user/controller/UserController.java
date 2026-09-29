package com.monetrax.monetrax.user.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.user.dto.*;
import com.monetrax.monetrax.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInformation> getUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /user/me [userId={}]", userId);
        try {
            UserInformation response = userService.fetchUserById(userId);
            log.debug("GET /user/me succeeded [userId={}]", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("GET /user/me failed [userId={}]", userId, e);
            throw e;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UserInformation> createUser(@Valid @RequestBody UserCreation req) {
        log.info("Endpoint called: POST /user/create");
        try {
            UserInformation userInfo = userService.createUser(req);
            log.info("POST /user/create succeeded [userId={}]", userInfo.getUserId());
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("POST /user/create failed", e);
            throw e;
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<UserInformation> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody UserUpdate req) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PATCH /user/update [userId={}]", userId);
        try {
            UserInformation response = userService.updateUser(req, userId);
            log.info("PATCH /user/update succeeded [userId={}]", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("PATCH /user/update failed [userId={}]", userId, e);
            throw e;
        }
    }

    @PatchMapping("/update/password")
    public ResponseEntity<UserSuccessfulPasswordUpdate> updateUserPassword(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody UserUpdatePassword req) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PATCH /user/update/password [userId={}]", userId);
        try {
            UserSuccessfulPasswordUpdate response = userService.updatePassword(req.getNewPassword(), req.getOldPassword(), userId);
            log.info("PATCH /user/update/password succeeded [userId={}]", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("PATCH /user/update/password failed [userId={}]: {}", userId, e.getMessage());
            throw e;
        }
    }
}