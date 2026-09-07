package com.monetrax.monetrax.auth.controller;

import com.monetrax.monetrax.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody ){

    }








}
