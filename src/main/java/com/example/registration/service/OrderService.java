package com.example.registration.service;

import com.example.registration.dto.OrderResponseDto;
import java.math.BigDecimal;

public interface OrderService {
    void createPendingOrder(String orderId, Integer userId, BigDecimal totalAmount);
    void completeOrder(String orderId);
    OrderResponseDto getUserOrders(Integer userId, String username);
}
