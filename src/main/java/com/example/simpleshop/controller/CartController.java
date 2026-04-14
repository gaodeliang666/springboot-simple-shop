package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.context.UserContext;
import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts")
    public Result<List<Cart>> findByUserId() {
        Long userId = UserContext.getUserId();
        return Result.success(cartService.findByUserId(userId));
    }

    @PostMapping("/carts")
    public Result<Void> addToCart(@RequestBody Cart cart) {
        cart.setUserId(UserContext.getUserId());
        int result = cartService.addToCart(cart);
        return result > 0 ? Result.success() : Result.error("加入购物车失败");
    }

    @PutMapping("/carts")
    public Result<Void> updateQuantity(@RequestBody Cart cart) {
        cart.setUserId(UserContext.getUserId());
        int result = cartService.updateQuantity(cart);
        return result > 0 ? Result.success() : Result.error("修改购物车数量失败");
    }

    @DeleteMapping("/carts/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        Long currentUserId = UserContext.getUserId();
        Cart cart = cartService.findById(id);

        if (cart == null) {
            return Result.error("购物车记录不存在");
        }

        if (!currentUserId.equals(cart.getUserId())) {
            return Result.error("无权操作他人的购物车");
        }

        int result = cartService.deleteById(id);
        return result > 0 ? Result.success() : Result.error("删除购物车商品失败");
    }


}