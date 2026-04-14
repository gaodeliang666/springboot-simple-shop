package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.service.RedisCartService;
import org.springframework.web.bind.annotation.*;
import com.example.simpleshop.vo.RedisCartVO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis/carts")
public class RedisCartController {

    private final RedisCartService redisCartService;

    public RedisCartController(RedisCartService redisCartService) {
        this.redisCartService = redisCartService;
    }

    @PostMapping
    public Result<Void> addToCart(@RequestBody Cart cart) {
        redisCartService.addToCart(cart);
        return Result.success();
    }

    @GetMapping("/{userId}")
    public Result<List<RedisCartVO>> findByUserId(@PathVariable Long userId) {
        return Result.success(redisCartService.findByUserId(userId));
    }

    @PutMapping
    public Result<Void> updateQuantity(@RequestBody Map<String, Object> param) {
        Long userId = Long.valueOf(param.get("userId").toString());
        Long productId = Long.valueOf(param.get("productId").toString());
        Integer quantity = Integer.valueOf(param.get("quantity").toString());

        redisCartService.updateQuantity(userId, productId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{userId}/{productId}")
    public Result<Void> deleteByUserIdAndProductId(@PathVariable Long userId,
                                                   @PathVariable Long productId) {
        redisCartService.deleteByUserIdAndProductId(userId, productId);
        return Result.success();
    }
}