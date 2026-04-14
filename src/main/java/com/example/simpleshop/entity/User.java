package com.example.simpleshop.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}