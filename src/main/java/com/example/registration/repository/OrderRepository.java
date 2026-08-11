package com.example.registration.repository;

import com.example.registration.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, Order.OrderStatus status);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'SUCCESS' AND DATE(created_at) = :date", nativeQuery = true)
    BigDecimal findDailyRevenue(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'SUCCESS' AND YEAR(created_at) = :year AND MONTH(created_at) = :month", nativeQuery = true)
    BigDecimal findMonthlyRevenue(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'SUCCESS' AND YEAR(created_at) = :year", nativeQuery = true)
    BigDecimal findYearlyRevenue(@Param("year") int year);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'SUCCESS'", nativeQuery = true)
    BigDecimal findOverallRevenue();
}
