package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.mapper.CartMapper;
import com.example.simpleshop.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Override
    public List<Cart> findByUserId(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    @Override
    public int addToCart(Cart cart) {
        Cart existCart = cartMapper.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
        if (existCart == null) {
            return cartMapper.insert(cart);
        } else {
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            return cartMapper.updateQuantity(existCart);
        }
    }

    @Override
    public int updateQuantity(Cart cart) {
        return cartMapper.updateQuantity(cart);
    }

    @Override
    public int deleteById(Long id) {
        return cartMapper.deleteById(id);
    }

    @Override
    public Cart findById(Long id) {
        return cartMapper.findById(id);
    }
}