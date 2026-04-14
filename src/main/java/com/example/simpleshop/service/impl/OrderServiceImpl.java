package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.Cart;
import com.example.simpleshop.entity.Order;
import com.example.simpleshop.entity.OrderItem;
import com.example.simpleshop.entity.Product;
import com.example.simpleshop.mapper.CartMapper;
import com.example.simpleshop.mapper.OrderItemMapper;
import com.example.simpleshop.mapper.OrderMapper;
import com.example.simpleshop.mapper.ProductMapper;
import com.example.simpleshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.simpleshop.service.RedisCartService;
import com.example.simpleshop.exception.BusinessException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.example.simpleshop.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import com.example.simpleshop.constant.MqMessageTypeConstant;
import com.example.simpleshop.mq.message.OrderDelayMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final RedisCartService redisCartService;
    private final RabbitTemplate rabbitTemplate;

    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper,
                            ProductMapper productMapper,
                            CartMapper cartMapper,
                            RedisCartService redisCartService,
                            RabbitTemplate rabbitTemplate) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.redisCartService = redisCartService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public Long submitOrder(Long userId) {
        List<Cart> cartList = cartMapper.findByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new BusinessException("购物车为空，无法提交订单");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Cart cart : cartList) {
            Product product = productMapper.findById(cart.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在，商品id：" + cart.getProductId());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException("商品库存不足，商品：" + product.getName());
            }

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
            );
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("UNPAID");
        orderMapper.insert(order);

        for (Cart cart : cartList) {
            Product product = productMapper.findById(cart.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItemMapper.insert(orderItem);

            int result = productMapper.decreaseStock(product.getId(), cart.getQuantity());
            if (result <= 0) {
                throw new BusinessException("扣减库存失败，商品：" + product.getName());
            }
        }

        cartMapper.deleteByUserId(userId);

        sendDelayCancelOrderMessage(order.getId());

        return order.getId();
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public Order findDetailById(Long id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            order.setOrderItems(orderItemMapper.findByOrderId(id));
        }
        return order;
    }

    @Override
    public int updateStatus(Long id, String status) {
        return orderMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public int cancelById(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"UNPAID".equals(order.getStatus())) {
            throw new BusinessException("只有待支付订单才能取消");
        }

        List<OrderItem> orderItems = orderItemMapper.findByOrderId(id);
        for (OrderItem orderItem : orderItems) {
            int result = productMapper.increaseStock(orderItem.getProductId(), orderItem.getQuantity());
            if (result <= 0) {
                throw new BusinessException("恢复库存失败，商品id：" + orderItem.getProductId());
            }
        }

        return orderMapper.updateStatus(id, "CANCELLED");
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, String status) {
        return orderMapper.findByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional
    public Long submitRedisOrder(Long userId) {
        Map<Object, Object> cartMap = redisCartService.getCartMap(userId);
        if (cartMap == null || cartMap.isEmpty()) {
            throw new BusinessException("Redis购物车为空，无法提交订单");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer quantity = Integer.valueOf(entry.getValue().toString());

            Product product = productMapper.findById(productId);
            if (product == null) {
                throw new BusinessException("商品不存在，商品id：" + productId);
            }
            if (product.getStock() < quantity) {
                throw new BusinessException("商品库存不足，商品：" + product.getName());
            }

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(quantity))
            );
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("UNPAID");
        orderMapper.insert(order);

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer quantity = Integer.valueOf(entry.getValue().toString());

            Product product = productMapper.findById(productId);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItemMapper.insert(orderItem);

            int result = productMapper.decreaseStock(product.getId(), quantity);
            if (result <= 0) {
                throw new BusinessException("扣减库存失败，商品：" + product.getName());
            }
        }

        redisCartService.clearCart(userId);
        sendDelayCancelOrderMessage(order.getId());

        return order.getId();
    }

    private void sendDelayCancelOrderMessage(Long orderId) {
        OrderDelayMessage message = new OrderDelayMessage(
                orderId,
                MqMessageTypeConstant.ORDER_DELAY_CANCEL
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EVENT_EXCHANGE,
                RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                message
        );

        log.info("发送延迟取消订单消息成功，orderId={}", orderId);
    }
}