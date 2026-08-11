package com.example.registration.dto;

import java.util.List;

public class OrderResponseDto {

    private String role;
    private String username;
    private OrderWrapper orders;

    public OrderResponseDto() {
    }

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

    public OrderWrapper getOrders() {
        return orders;
    }

    public void setOrders(OrderWrapper orders) {
        this.orders = orders;
    }

    public static class OrderWrapper {
        private List<OrderProductDto> products;

        public OrderWrapper() {
        }

        public OrderWrapper(List<OrderProductDto> products) {
            this.products = products;
        }

        public List<OrderProductDto> getProducts() {
            return products;
        }

        public void setProducts(List<OrderProductDto> products) {
            this.products = products;
        }
    }
}
