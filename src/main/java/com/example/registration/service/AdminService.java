package com.example.registration.service;

import com.example.registration.entity.Product;
import com.example.registration.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface AdminService {
    
    // Product Management
    Product addProduct(String name, String description, BigDecimal price, Integer stock, Integer categoryId, String imageUrl);
    void deleteProduct(Integer productId);

    // User Management
    List<User> getAllUsers();
    User updateUser(Integer userId, String fullName, String email, String role);
    
    // Analytics
    BigDecimal getDailyRevenue(String date);
    BigDecimal getMonthlyRevenue(int year, int month);
    BigDecimal getYearlyRevenue(int year);
    BigDecimal getOverallRevenue();
}
