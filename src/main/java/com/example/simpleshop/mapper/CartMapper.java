package com.example.simpleshop.mapper;

import com.example.simpleshop.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("select " +
            "c.id, " +
            "c.user_id as userId, " +
            "c.product_id as productId, " +
            "c.quantity, " +
            "c.create_time as createTime, " +
            "c.update_time as updateTime, " +
            "p.name as productName, " +
            "p.price as productPrice, " +
            "p.description as productDescription " +
            "from cart c left join product p on c.product_id = p.id " +
            "where c.user_id = #{userId}")
    List<Cart> findByUserId(Long userId);

    @Select("select id, user_id as userId, product_id as productId, quantity, create_time as createTime, update_time as updateTime from cart where user_id = #{userId} and product_id = #{productId}")
    Cart findByUserIdAndProductId(Long userId, Long productId);

    @Insert("insert into cart(user_id, product_id, quantity) values(#{userId}, #{productId}, #{quantity})")
    int insert(Cart cart);

    @Update("update cart set quantity = #{quantity}, update_time = now() where id = #{id}")
    int updateQuantity(Cart cart);

    @Delete("delete from cart where id = #{id}")
    int deleteById(Long id);

    @Delete("delete from cart where user_id = #{userId}")
    int deleteByUserId(Long userId);
}