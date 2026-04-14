package com.example.simpleshop.listener;

import com.example.simpleshop.config.RabbitMQConfig;
import com.example.simpleshop.constant.OrderStatusConstant;
import com.example.simpleshop.entity.Order;
import com.example.simpleshop.mq.message.OrderDelayMessage;
import com.example.simpleshop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderReleaseListener {

    private final OrderService orderService;

    public OrderReleaseListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_RELEASE_QUEUE)
    public void handleOrderRelease(OrderDelayMessage message) {
        Long orderId = message.getOrderId();

        try {
            log.info("收到延迟取消订单消息，orderId={}, messageType={}",
                    message.getOrderId(), message.getMessageType());

            Order order = orderService.findDetailById(orderId);
            if (order == null) {
                log.warn("订单不存在，无需处理，orderId={}", orderId);
                return;
            }

            if (OrderStatusConstant.UNPAID.equals(order.getStatus())) {
                orderService.cancelById(orderId);
                log.info("订单超时未支付，自动取消成功，orderId={}", orderId);
            } else {
                log.info("订单状态不是UNPAID，无需取消，orderId={}, status={}", orderId, order.getStatus());
            }
        } catch (Exception e) {
            log.error("处理延迟取消订单消息失败，orderId={}", orderId, e);
            throw e;
        }
    }
}