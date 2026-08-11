package com.example.registration.service;

import com.example.registration.dto.CartItemRequest;
import com.example.registration.dto.CartProductDto;
import com.example.registration.dto.CartResponseDto;
import com.example.registration.entity.CartItem;
import com.example.registration.entity.Product;
import com.example.registration.entity.ProductImage;
import com.example.registration.entity.User;
import com.example.registration.repository.CartItemRepository;
import com.example.registration.repository.ProductRepository;
import com.example.registration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Integer getCartCount(Integer userId) {
        Integer count = cartItemRepository.countTotalQuantityByUserId(userId);
        return count != null ? count : 0;
    }

    @Transactional
    public void addToCart(CartItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findFirstByUser_IdAndProduct_Id(request.getUserId(), request.getProductId());

        int currentQuantity = existingItem.map(CartItem::getQuantity).orElse(0);
        int newQuantity = currentQuantity + request.getQuantity();

        if (newQuantity > product.getStock()) {
            throw new RuntimeException("Stock limit exceeded. Only " + product.getStock() + " products are available.");
        }

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    public CartResponseDto getCartItems(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> items = cartItemRepository.findByUser_Id(userId);

        BigDecimal overallTotal = BigDecimal.ZERO;
        List<CartProductDto> productDtos = items.stream().map(item -> {
            Product p = item.getProduct();
            CartProductDto dto = new CartProductDto();
            dto.setProduct_id(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            
            // Get first image if available
            String imageUrl = "";
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                imageUrl = p.getImages().get(0).getImageUrl();
            }
            dto.setImage_url(imageUrl);
            
            dto.setPrice_per_unit(p.getPrice());
            dto.setQuantity(item.getQuantity());
            
            BigDecimal itemTotal = p.getPrice().multiply(new BigDecimal(item.getQuantity()));
            dto.setTotal_price(itemTotal);
            return dto;
        }).collect(Collectors.toList());

        for (CartProductDto dto : productDtos) {
            overallTotal = overallTotal.add(dto.getTotal_price());
        }

        CartResponseDto.CartDetails details = new CartResponseDto.CartDetails();
        details.setOverall_total_price(overallTotal);
        details.setProducts(productDtos);

        CartResponseDto response = new CartResponseDto();
        response.setRole("CUSTOMER"); // Fixed for now
        response.setUsername(user.getFullName());
        response.setCart(details);

        return response;
    }

    @Transactional
    public void updateCartQuantity(Integer userId, Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getStock()) {
            throw new RuntimeException("Stock limit exceeded. Only " + product.getStock() + " products are available.");
        }

        Optional<CartItem> existingItem = cartItemRepository.findFirstByUser_IdAndProduct_Id(userId, productId);
        
        if (quantity <= 0) {
            existingItem.ifPresent(cartItemRepository::delete);
        } else {
            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            } else {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                CartItem cartItem = new CartItem();
                cartItem.setUser(user);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    @Transactional
    public void deleteFromCart(Integer userId, Integer productId) {
        Optional<CartItem> existingItem = cartItemRepository.findFirstByUser_IdAndProduct_Id(userId, productId);
        existingItem.ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clearCart(Integer userId) {
        List<CartItem> items = cartItemRepository.findByUser_Id(userId);
        cartItemRepository.deleteAll(items);
    }
}
