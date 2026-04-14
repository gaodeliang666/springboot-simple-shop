package com.example.simpleshop.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 订单详情列表
    private List<OrderItem> orderItems;
}