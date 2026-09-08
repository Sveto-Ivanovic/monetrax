package com.monetrax.monetrax.auth.controller;


import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

        @Autowired
        private AuthService authService;


        @PostMapping("/login")
        public ResponseEntity<AuthResponse> authenticateAndGetToken(@Valid @RequestBody AuthRequest authInfo){
            AuthResponse authResponse = authService.login(authInfo);
            return ResponseEntity.ok(authResponse);
        }


        @GetMapping("/test")
        public ResponseEntity<String> authenticateAndGetToken() {
            return ResponseEntity.ok("Test");
        }
    }
