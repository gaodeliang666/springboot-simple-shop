package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.User;
import com.example.simpleshop.mapper.UserMapper;
import com.example.simpleshop.service.UserService;
import com.example.simpleshop.util.JwtUtil;
import org.springframework.stereotype.Service;
import com.example.simpleshop.exception.BusinessException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public int insert(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}