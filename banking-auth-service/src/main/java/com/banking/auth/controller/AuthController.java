package com.banking.auth.controller;

import org.springframework.web.bind.annotation.*;

import com.banking.auth.model.LoginRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        return "Login API working";
    }
}