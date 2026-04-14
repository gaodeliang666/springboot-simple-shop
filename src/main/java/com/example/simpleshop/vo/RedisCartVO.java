package com.example.simpleshop.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedisCartVO {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
}