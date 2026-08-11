package com.example.registration.controller;

import com.example.registration.dto.UserRegistrationRequest;
import com.example.registration.dto.UserRegistrationResponse;
import com.example.registration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<com.example.registration.dto.UserLoginResponse> login(@Valid @RequestBody com.example.registration.dto.UserLoginRequest request) {
        com.example.registration.dto.UserLoginResponse response = userService.loginUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
