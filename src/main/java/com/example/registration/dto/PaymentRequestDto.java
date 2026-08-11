package com.example.registration.dto;

import java.math.BigDecimal;

public class PaymentRequestDto {
    private BigDecimal amount;
    private Integer userId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
