package com.example.simpleshop.mq.message;

public class OrderDelayMessage {

    private Long orderId;
    private String messageType;

    public OrderDelayMessage() {
    }

    public OrderDelayMessage(Long orderId, String messageType) {
        this.orderId = orderId;
        this.messageType = messageType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}