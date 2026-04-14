package com.example.simpleshop.service;

import com.example.simpleshop.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(Long id);

    int insert(Product product);

    int update(Product product);

    int deleteById(Long id);
}