package com.example.simpleshop.controller;

import com.example.simpleshop.common.Result;
import com.example.simpleshop.entity.Product;
import com.example.simpleshop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public Result<List<Product>> findAll() {
        return Result.success(productService.findAll());
    }

    @GetMapping("/products/{id}")
    public Result<Product> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping("/products")
    public Result<Void> insert(@RequestBody Product product) {
        int result = productService.insert(product);
        return result > 0 ? Result.success() : Result.error("新增商品失败");
    }

    @PutMapping("/products")
    public Result<Void> update(@RequestBody Product product) {
        int result = productService.update(product);
        return result > 0 ? Result.success() : Result.error("修改商品失败");
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        int result = productService.deleteById(id);
        return result > 0 ? Result.success() : Result.error("删除商品失败");
    }
}