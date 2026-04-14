package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.constant.OrderStatusConstant;
import com.example.simpleshop.entity.Order;
import com.example.simpleshop.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/{userId}")
    public Result<Long> submitOrder(@PathVariable Long userId) {
        return Result.success(orderService.submitOrder(userId));
    }

    @PostMapping("/orders/redis/{userId}")
    public Result<Long> submitRedisOrder(@PathVariable Long userId) {
        return Result.success(orderService.submitRedisOrder(userId));
    }

    @GetMapping("/orders/user/{userId}")
    public Result<List<Order>> findByUserId(@PathVariable Long userId) {
        return Result.success(orderService.findByUserId(userId));
    }

    @GetMapping("/orders/{id}")
    public Result<Order> findDetailById(@PathVariable Long id) {
        Order order = orderService.findDetailById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @PutMapping("/orders/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> param) {
        Long id = Long.valueOf(param.get("id").toString());
        String status = param.get("status").toString();

        int result = orderService.updateStatus(id, status);
        return result > 0 ? Result.success() : Result.error("修改订单状态失败");
    }

    @PutMapping("/orders/cancel/{id}")
    public Result<Void> cancelById(@PathVariable Long id) {
        orderService.cancelById(id);
        return Result.success();
    }

    @GetMapping("/orders/user/{userId}/status/{status}")
    public Result<List<Order>> findByUserIdAndStatus(@PathVariable Long userId,
                                                     @PathVariable String status) {
        if (!OrderStatusConstant.UNPAID.equals(status)
                && !OrderStatusConstant.PAID.equals(status)
                && !OrderStatusConstant.CANCELLED.equals(status)) {
            return Result.error("订单状态不合法");
        }
        return Result.success(orderService.findByUserIdAndStatus(userId, status));
    }


}