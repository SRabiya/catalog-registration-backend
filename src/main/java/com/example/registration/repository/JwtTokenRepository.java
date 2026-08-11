package com.example.registration.repository;

import com.example.registration.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {
    List<JwtToken> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}
