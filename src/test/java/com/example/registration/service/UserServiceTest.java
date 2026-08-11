package com.example.registration.service;

import com.example.registration.dto.UserRegistrationRequest;
import com.example.registration.dto.UserRegistrationResponse;
import com.example.registration.entity.User;
import com.example.registration.exception.DuplicateResourceException;
import com.example.registration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("Asha Rao");
        request.setEmail("asha@example.com");
        request.setPassword("StrongPass123!");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setFullName("Asha Rao");
        savedUser.setEmail("asha@example.com");
        savedUser.setPasswordHash("hashedpassword");

        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Asha Rao", response.getFullName());
        assertEquals("asha@example.com", response.getEmail());

        verify(passwordEncoder).encode("StrongPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("asha@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
