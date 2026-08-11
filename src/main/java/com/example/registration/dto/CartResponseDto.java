package com.example.registration.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {
    private String role;
    private String username;
    private CartDetails cart;

    public CartResponseDto() {}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CartDetails getCart() {
        return cart;
    }

    public void setCart(CartDetails cart) {
        this.cart = cart;
    }

    public static class CartDetails {
        private BigDecimal overall_total_price;
        private List<CartProductDto> products;

        public CartDetails() {}

        public BigDecimal getOverall_total_price() {
            return overall_total_price;
        }

        public void setOverall_total_price(BigDecimal overall_total_price) {
            this.overall_total_price = overall_total_price;
        }

        public List<CartProductDto> getProducts() {
            return products;
        }

        public void setProducts(List<CartProductDto> products) {
            this.products = products;
        }
    }
}
