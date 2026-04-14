package com.example.simpleshop.service;

import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.vo.RedisCartVO;

import java.util.List;
import java.util.Map;

public interface RedisCartService {
    void addToCart(Cart cart);

    List<RedisCartVO> findByUserId(Long userId);

    void updateQuantity(Long userId, Long productId, Integer quantity);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    Map<Object, Object> getCartMap(Long userId);

    void clearCart(Long userId);
}