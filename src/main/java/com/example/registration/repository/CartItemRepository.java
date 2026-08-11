package com.example.registration.repository;

import com.example.registration.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser_Id(Integer userId);
    
    Optional<CartItem> findFirstByUser_IdAndProduct_Id(Integer userId, Integer productId);

    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user.id = :userId")
    Integer countTotalQuantityByUserId(@Param("userId") Integer userId);
}
