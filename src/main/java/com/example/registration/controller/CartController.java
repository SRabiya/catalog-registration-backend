package com.example.registration.controller;

import com.example.registration.dto.CartItemRequest;
import com.example.registration.dto.CartResponseDto;
import com.example.registration.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/items/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@RequestParam Integer userId) {
        Integer count = cartService.getCartCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            cartService.addToCart(request);
            return ResponseEntity.ok(Map.of("message", "Item added to cart successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/items")
    public ResponseEntity<CartResponseDto> getCartItems(@RequestParam Integer userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateCartQuantity(@RequestBody CartItemRequest request) {
        try {
            cartService.updateCartQuantity(request.getUserId(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(Map.of("message", "Cart updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteCartItem(
            @RequestParam Integer userId, 
            @RequestParam Integer productId) {
        cartService.deleteFromCart(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart(@RequestParam Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
    }
}
