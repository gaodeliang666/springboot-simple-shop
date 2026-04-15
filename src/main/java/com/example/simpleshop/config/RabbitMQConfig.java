package com.example.simpleshop.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";

    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay";

    public static final String ORDER_RELEASE_QUEUE = "order.release.queue";
    public static final String ORDER_RELEASE_ROUTING_KEY = "order.release";

    public RabbitMQConfig() {
        System.out.println("RabbitMQConfig 已加载");
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.initialize();
        System.out.println("RabbitAdmin 已创建并初始化");
        return rabbitAdmin;
    }

    @Bean
    public DirectExchange orderEventExchange() {
        System.out.println("开始创建交换机: " + ORDER_EVENT_EXCHANGE);
        return new DirectExchange(ORDER_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderDelayQueue() {
        System.out.println("开始创建队列: " + ORDER_DELAY_QUEUE);
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ORDER_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_RELEASE_ROUTING_KEY);
        args.put("x-message-ttl", 600000);
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue orderReleaseQueue() {
        System.out.println("开始创建队列: " + ORDER_RELEASE_QUEUE);
        return new Queue(ORDER_RELEASE_QUEUE, true);
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderEventExchange())
                .with(ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderReleaseBinding() {
        return BindingBuilder.bind(orderReleaseQueue())
                .to(orderEventExchange())
                .with(ORDER_RELEASE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}