package com.example.registration.dto;

import java.math.BigDecimal;

public class CartProductDto {
    private Integer product_id;
    private String name;
    private String description;
    private String image_url;
    private BigDecimal price_per_unit;
    private Integer quantity;
    private BigDecimal total_price;

    public CartProductDto() {}

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public BigDecimal getPrice_per_unit() {
        return price_per_unit;
    }

    public void setPrice_per_unit(BigDecimal price_per_unit) {
        this.price_per_unit = price_per_unit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}
