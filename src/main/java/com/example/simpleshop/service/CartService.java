package com.example.simpleshop.service;

import com.example.simpleshop.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findByUserId(Long userId);

    int addToCart(Cart cart);

    int updateQuantity(Cart cart);

    int deleteById(Long id);

    Cart findById(Long id);
}