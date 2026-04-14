package com.example.simpleshop.mapper;

import com.example.simpleshop.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("select id, name, price, stock, description, create_time as createTime, update_time as updateTime from product")
    List<Product> findAll();

    @Select("select id, name, price, stock, description, create_time as createTime, update_time as updateTime from product where id = #{id}")
    Product findById(Long id);

    @Insert("insert into product(name, price, stock, description) values(#{name}, #{price}, #{stock}, #{description})")
    int insert(Product product);

    @Update("update product set name=#{name}, price=#{price}, stock=#{stock}, description=#{description}, update_time = now() where id=#{id}")
    int update(Product product);

    @Delete("delete from product where id = #{id}")
    int deleteById(Long id);

    @Update("update product set stock = stock - #{quantity}, update_time = now() where id = #{productId} and stock >= #{quantity}")
    int decreaseStock(Long productId, Integer quantity);

    @Update("update product set stock = stock + #{quantity}, update_time = now() where id = #{productId}")
    int increaseStock(Long productId, Integer quantity);
}