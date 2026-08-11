package com.example.registration.service;

import com.example.registration.dto.UserRegistrationRequest;
import com.example.registration.dto.UserRegistrationResponse;

import com.example.registration.dto.UserLoginRequest;
import com.example.registration.dto.UserLoginResponse;

public interface UserService {
    UserRegistrationResponse registerUser(UserRegistrationRequest request);
    UserLoginResponse loginUser(UserLoginRequest request);
}
