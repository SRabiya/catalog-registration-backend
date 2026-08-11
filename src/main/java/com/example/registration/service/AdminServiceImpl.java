package com.example.registration.service;

import com.example.registration.entity.Category;
import com.example.registration.entity.Product;
import com.example.registration.entity.User;
import com.example.registration.repository.CategoryRepository;
import com.example.registration.repository.OrderRepository;
import com.example.registration.repository.ProductRepository;
import com.example.registration.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(ProductRepository productRepository, 
                            CategoryRepository categoryRepository, 
                            UserRepository userRepository, 
                            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Product addProduct(String name, String description, BigDecimal price, Integer stock, Integer categoryId, String imageUrl) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));
            
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        
        // If image logic is needed, it would be added to ProductImage entity or Product entity directly.
        // Assuming imageUrl is handled separately if there's no direct field in Product right now,
        // but we'll add logic if required. The entity Product has List<ProductImage> images.
        
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Integer userId, String fullName, String email, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        if (fullName != null) user.setFullName(fullName);
        if (email != null) user.setEmail(email);
        if (role != null) user.setRole(role);
        
        return userRepository.save(user);
    }

    @Override
    public BigDecimal getDailyRevenue(String date) {
        return orderRepository.findDailyRevenue(date);
    }

    @Override
    public BigDecimal getMonthlyRevenue(int year, int month) {
        return orderRepository.findMonthlyRevenue(year, month);
    }

    @Override
    public BigDecimal getYearlyRevenue(int year) {
        return orderRepository.findYearlyRevenue(year);
    }

    @Override
    public BigDecimal getOverallRevenue() {
        return orderRepository.findOverallRevenue();
    }
}
