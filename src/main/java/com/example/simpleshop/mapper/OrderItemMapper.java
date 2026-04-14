package com.example.simpleshop.mapper;

import com.example.simpleshop.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("insert into order_item(order_id, product_id, product_name, price, quantity) values(#{orderId}, #{productId}, #{productName}, #{price}, #{quantity})")
    int insert(OrderItem orderItem);

    @Select("select id, order_id as orderId, product_id as productId, product_name as productName, price, quantity, create_time as createTime, update_time as updateTime from order_item where order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);
}