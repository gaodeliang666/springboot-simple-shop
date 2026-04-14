package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.entity.User;
import com.example.simpleshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/users/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping("/users")
    public Result<Void> insert(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        int result = userService.insert(user);
        return result > 0 ? Result.success() : Result.error("新增失败");
    }

    @PutMapping("/users")
    public Result<Void> update(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        int result = userService.update(user);
        return result > 0 ? Result.success() : Result.error("修改失败");
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        int result = userService.deleteById(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }
}