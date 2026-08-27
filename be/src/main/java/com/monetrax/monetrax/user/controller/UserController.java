package com.monetrax.monetrax.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    public String randomvar = null;

    @GetMapping("/user/fetch")
    public String getUser(){
        return "Hello world";
    }

}
