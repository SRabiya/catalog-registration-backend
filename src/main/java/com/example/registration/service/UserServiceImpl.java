package com.example.registration.service;

import com.example.registration.dto.UserRegistrationRequest;
import com.example.registration.dto.UserRegistrationResponse;
import com.example.registration.entity.User;
import com.example.registration.exception.DuplicateResourceException;
import com.example.registration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.registration.security.JwtUtil jwtUtil;
    private final com.example.registration.repository.JwtTokenRepository jwtTokenRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, com.example.registration.security.JwtUtil jwtUtil, com.example.registration.repository.JwtTokenRepository jwtTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        String email = request.getEmail().toLowerCase();
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already in use");
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;
    }

    @Override
    public com.example.registration.dto.UserLoginResponse loginUser(com.example.registration.dto.UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new com.example.registration.exception.InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new com.example.registration.exception.InvalidCredentialsException("Invalid email or password");
        }

        com.example.registration.dto.UserLoginResponse response = new com.example.registration.dto.UserLoginResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getFullName());
        
        com.example.registration.entity.JwtToken jwtEntity = new com.example.registration.entity.JwtToken(token, user, java.time.LocalDateTime.now().plusHours(1));
        jwtTokenRepository.save(jwtEntity);
        
        response.setToken(token);
        
        return response;
    }
}
