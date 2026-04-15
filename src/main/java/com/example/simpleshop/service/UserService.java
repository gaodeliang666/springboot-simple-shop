package com.example.simpleshop.service;

import com.example.simpleshop.entity.User;
import com.example.simpleshop.vo.UserVO;

import java.util.List;

public interface UserService {
    List<UserVO> findAll();

    UserVO findById(Long id);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    String login(String username, String password);
}