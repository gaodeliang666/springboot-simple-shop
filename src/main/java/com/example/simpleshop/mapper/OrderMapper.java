package com.example.simpleshop.mapper;

import com.example.simpleshop.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    @Insert("insert into orders(user_id, total_amount, status) values(#{userId}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("select id, user_id as userId, total_amount as totalAmount, status, create_time as createTime, update_time as updateTime from orders where user_id = #{userId} order by id desc")
    List<Order> findByUserId(Long userId);

    @Select("select id, user_id as userId, total_amount as totalAmount, status, create_time as createTime, update_time as updateTime from orders where id = #{id}")
    Order findById(Long id);

    @Select("select id, user_id as userId, total_amount as totalAmount, status, create_time as createTime, update_time as updateTime from orders where user_id = #{userId} and status = #{status} order by id desc")
    List<Order> findByUserIdAndStatus(Long userId, String status);

    @Update("update orders set status = #{newStatus}, update_time = now() " +
            "where id = #{id} and status = #{oldStatus}")
    int updateStatusCondition(@Param("id") Long id,
                              @Param("oldStatus") String oldStatus,
                              @Param("newStatus") String newStatus);
}