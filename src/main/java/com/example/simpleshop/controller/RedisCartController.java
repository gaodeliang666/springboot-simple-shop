package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.context.UserContext;
import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.service.RedisCartService;
import com.example.simpleshop.vo.RedisCartVO;
import org.springframework.web.bind.annotation.*;

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
        cart.setUserId(UserContext.getUserId());
        redisCartService.addToCart(cart);
        return Result.success();
    }

    @GetMapping
    public Result<List<RedisCartVO>> findByUserId() {
        Long userId = UserContext.getUserId();
        return Result.success(redisCartService.findByUserId(userId));
    }

    @PutMapping
    public Result<Void> updateQuantity(@RequestBody Map<String, Object> param) {
        Long userId = UserContext.getUserId();
        Long productId = Long.valueOf(param.get("productId").toString());
        Integer quantity = Integer.valueOf(param.get("quantity").toString());

        redisCartService.updateQuantity(userId, productId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> deleteByUserIdAndProductId(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        redisCartService.deleteByUserIdAndProductId(userId, productId);
        return Result.success();
    }
}