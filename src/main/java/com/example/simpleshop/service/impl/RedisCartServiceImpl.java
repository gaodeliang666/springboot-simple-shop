package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.entity.Product;
import com.example.simpleshop.mapper.ProductMapper;
import com.example.simpleshop.service.RedisCartService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.simpleshop.vo.RedisCartVO;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCartServiceImpl implements RedisCartService {

    private static final long CART_EXPIRE_DAYS = 7;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductMapper productMapper;

    private void refreshExpire(String key) {
        redisTemplate.expire(key, CART_EXPIRE_DAYS, TimeUnit.DAYS);
    }


    public RedisCartServiceImpl(RedisTemplate<String, Object> redisTemplate, ProductMapper productMapper) {
        this.redisTemplate = redisTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public void addToCart(Cart cart) {
        String key = "cart:" + cart.getUserId();
        String productId = cart.getProductId().toString();

        Object value = redisTemplate.opsForHash().get(key, productId);
        if (value == null) {
            redisTemplate.opsForHash().put(key, productId, cart.getQuantity().toString());
        } else {
            int oldQuantity = Integer.parseInt(value.toString());
            int newQuantity = oldQuantity + cart.getQuantity();
            redisTemplate.opsForHash().put(key, productId, String.valueOf(newQuantity));
        }

        refreshExpire(key);
    }

    @Override
    public List<RedisCartVO> findByUserId(Long userId) {
        String key = "cart:" + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        refreshExpire(key);

        List<RedisCartVO> cartList = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer quantity = Integer.valueOf(entry.getValue().toString());

            Product product = productMapper.findById(productId);

            RedisCartVO vo = new RedisCartVO();
            vo.setUserId(userId);
            vo.setProductId(productId);
            vo.setQuantity(quantity);

            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductPrice(product.getPrice());
                vo.setProductDescription(product.getDescription());
            }

            cartList.add(vo);
        }

        return cartList;
    }

    @Override
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        String key = "cart:" + userId;
        redisTemplate.opsForHash().put(key, productId.toString(), quantity.toString());
        refreshExpire(key);
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        String key = "cart:" + userId;
        redisTemplate.opsForHash().delete(key, productId.toString());

        Long size = redisTemplate.opsForHash().size(key);
        if (size != null && size > 0) {
            refreshExpire(key);
        }
    }

    @Override
    public Map<Object, Object> getCartMap(Long userId) {
        String key = "cart:" + userId;
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void clearCart(Long userId) {
        String key = "cart:" + userId;
        redisTemplate.delete(key);
    }
}