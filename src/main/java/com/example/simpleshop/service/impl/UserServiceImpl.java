package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.User;
import com.example.simpleshop.exception.BusinessException;
import com.example.simpleshop.mapper.UserMapper;
import com.example.simpleshop.service.UserService;
import com.example.simpleshop.util.JwtUtil;
import com.example.simpleshop.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserVO> findAll() {
        return toUserVOList(userMapper.findAll());
    }

    @Override
    public UserVO findById(Long id) {
        return toUserVO(userMapper.findById(id));
    }

    @Override
    public int insert(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }

    private UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setPhone(user.getPhone());
        return userVO;
    }

    private List<UserVO> toUserVOList(List<User> userList) {
        return userList.stream().map(this::toUserVO).toList();
    }
}