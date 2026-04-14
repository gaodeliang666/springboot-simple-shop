package com.example.simpleshop.mapper;

import com.example.simpleshop.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select id, username, password, nickname, phone, create_time as createTime, update_time as updateTime from user")
    List<User> findAll();

    @Select("select id, username, password, nickname, phone, create_time as createTime, update_time as updateTime from user where id = #{id}")
    User findById(Long id);

    @Insert("insert into user(username, password, nickname, phone) values(#{username}, #{password}, #{nickname}, #{phone})")
    int insert(User user);

    @Update("update user set username=#{username}, password=#{password}, nickname=#{nickname}, phone=#{phone} where id=#{id}")
    int update(User user);

    @Delete("delete from user where id = #{id}")
    int deleteById(Long id);
}