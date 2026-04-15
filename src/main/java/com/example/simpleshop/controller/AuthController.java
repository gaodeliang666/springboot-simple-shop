package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.dto.LoginRequest;
import com.example.simpleshop.dto.RegisterRequest;
import com.example.simpleshop.entity.User;
import com.example.simpleshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());

        int result = userService.insert(user);
        return result > 0 ? Result.success() : Result.error("注册失败");
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }
}