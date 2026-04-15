package com.example.simpleshop.service;

import com.example.simpleshop.entity.Order;

import java.util.List;

public interface OrderService {
    Long submitOrder(Long userId);

    List<Order> findByUserId(Long userId);

    Order findDetailById(Long id);

    int cancelById(Long id);

    List<Order> findByUserIdAndStatus(Long userId, String status);

    Long submitRedisOrder(Long userId);

    void payOrder(Long id);
}