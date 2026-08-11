package com.example.registration.service;

import com.example.registration.dto.OrderProductDto;
import com.example.registration.dto.OrderResponseDto;
import com.example.registration.entity.*;
import com.example.registration.repository.CartItemRepository;
import com.example.registration.repository.OrderItemRepository;
import com.example.registration.repository.OrderRepository;
import com.example.registration.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createPendingOrder(String orderId, Integer userId, BigDecimal totalAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void completeOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.SUCCESS) {
            return;
        }

        order.setStatus(Order.OrderStatus.SUCCESS);
        orderRepository.save(order);

        List<CartItem> cartItems = cartItemRepository.findByUser_Id(order.getUser().getId());
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
            
            BigDecimal totalPrice = cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            orderItem.setTotalPrice(totalPrice);
            
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(cartItems);
    }

    @Override
    public OrderResponseDto getUserOrders(Integer userId, String username) {
        List<Order> orders = orderRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, Order.OrderStatus.SUCCESS);

        List<OrderProductDto> productDtos = new ArrayList<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                OrderProductDto dto = new OrderProductDto();
                dto.setOrderId(order.getOrderId());
                dto.setProductId(item.getProduct().getId());
                dto.setName(item.getProduct().getName());
                dto.setDescription(item.getProduct().getDescription());
                dto.setQuantity(item.getQuantity());
                dto.setPricePerUnit(item.getPricePerUnit());
                dto.setTotalPrice(item.getTotalPrice());
                
                String imageUrl = "";
                if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                    imageUrl = item.getProduct().getImages().get(0).getImageUrl();
                }
                dto.setImageUrl(imageUrl);
                
                productDtos.add(dto);
            }
        }

        OrderResponseDto response = new OrderResponseDto();
        response.setRole("CUSTOMER"); // Static role for now based on spec
        response.setUsername(username);
        response.setOrders(new OrderResponseDto.OrderWrapper(productDtos));
        
        return response;
    }
}
