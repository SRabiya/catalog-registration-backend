package com.example.registration.controller;

import com.example.registration.entity.User;
import com.example.registration.service.LogoutService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LogoutService logoutService;

    public AuthController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Retrieve authenticated user set by the JwtAuthenticationInterceptor
            User user = (User) request.getAttribute("authenticatedUser");
            if (user != null) {
                // Delete the JWT token(s) from the database
                logoutService.logout(user.getId());
            }

            // Clear the authentication cookie per specification
            Cookie authCookie = new Cookie("jwt", null);
            authCookie.setMaxAge(0);
            authCookie.setPath("/");
            authCookie.setHttpOnly(true);
            response.addCookie(authCookie);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Logout successful");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "Logout failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
}
