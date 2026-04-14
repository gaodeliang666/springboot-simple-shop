package com.example.simpleshop.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 联表查询返回的商品信息
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
}