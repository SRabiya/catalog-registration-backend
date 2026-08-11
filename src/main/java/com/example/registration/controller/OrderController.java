package com.example.registration.controller;

import com.example.registration.dto.OrderResponseDto;
import com.example.registration.security.JwtUtil;
import com.example.registration.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<OrderResponseDto> getOrders(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        try {
            Integer userId = jwtUtil.extractClaim(token, claims -> (Integer) claims.get("userId"));
            String username = jwtUtil.extractClaim(token, claims -> (String) claims.get("fullName"));
            
            OrderResponseDto response = orderService.getUserOrders(userId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
