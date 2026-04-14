package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.dto.LoginRequest;
import com.example.simpleshop.service.UserService;
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

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }
}