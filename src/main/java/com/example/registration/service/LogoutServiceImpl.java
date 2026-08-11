package com.example.registration.service;

import com.example.registration.repository.JwtTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutServiceImpl implements LogoutService {

    private final JwtTokenRepository jwtTokenRepository;

    public LogoutServiceImpl(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    @Transactional
    public void logout(Integer userId) {
        // Find and delete all tokens for the user to invalidate session
        jwtTokenRepository.deleteByUserId(userId);
    }
}
