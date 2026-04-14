package com.example.simpleshop.service.impl;

import com.example.simpleshop.entity.Product;
import com.example.simpleshop.mapper.ProductMapper;
import com.example.simpleshop.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";
    private static final String PRODUCT_LIST_CACHE_KEY = "product:list";
    private static final long PRODUCT_CACHE_TTL = 30;
    private static final String PRODUCT_NULL_VALUE = "null";
    private static final long PRODUCT_NULL_TTL = 2;


    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductServiceImpl(ProductMapper productMapper,
                              RedisTemplate<String, Object> redisTemplate) {
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
    }

    private void deleteProductCache(Long productId) {
        String key = PRODUCT_CACHE_KEY_PREFIX + productId;
        redisTemplate.delete(key);
        log.info("删除商品详情缓存，key={}", key);
    }

    private void deleteProductListCache() {
        redisTemplate.delete(PRODUCT_LIST_CACHE_KEY);
        log.info("删除商品列表缓存，key={}", PRODUCT_LIST_CACHE_KEY);
    }

    @Override
    public List<Product> findAll() {
        Object cacheObject = redisTemplate.opsForValue().get(PRODUCT_LIST_CACHE_KEY);

        if (cacheObject != null) {
            log.info("命中商品列表缓存，key={}", PRODUCT_LIST_CACHE_KEY);
            return (List<Product>) cacheObject;
        }

        log.info("商品列表缓存未命中，开始查询MySQL");
        List<Product> productList = productMapper.findAll();

        if (productList != null && !productList.isEmpty()) {
            redisTemplate.opsForValue().set(PRODUCT_LIST_CACHE_KEY, productList, PRODUCT_CACHE_TTL, TimeUnit.MINUTES);
            log.info("商品列表写入Redis成功，key={}", PRODUCT_LIST_CACHE_KEY);
        }

        return productList;
    }

    @Override
    public Product findById(Long id) {
        String key = PRODUCT_CACHE_KEY_PREFIX + id;

        Object cacheObject = redisTemplate.opsForValue().get(key);

        if (cacheObject != null) {
            if (PRODUCT_NULL_VALUE.equals(cacheObject)) {
                log.info("命中空缓存，商品不存在，key={}", key);
                return null;
            }

            log.info("命中商品详情缓存，key={}", key);
            return (Product) cacheObject;
        }

        log.info("商品详情缓存未命中，开始查询MySQL，productId={}", id);
        Product product = productMapper.findById(id);

        if (product != null) {
            redisTemplate.opsForValue().set(key, product, PRODUCT_CACHE_TTL, TimeUnit.MINUTES);
            log.info("商品详情写入Redis成功，key={}", key);
            return product;
        }

        redisTemplate.opsForValue().set(key, PRODUCT_NULL_VALUE, PRODUCT_NULL_TTL, TimeUnit.MINUTES);
        log.info("商品不存在，写入空缓存，key={}", key);

        return null;
    }

    @Override
    public int insert(Product product) {
        int result = productMapper.insert(product);
        if (result > 0) {
            if (product.getId() != null) {
                deleteProductCache(product.getId());
            }
            deleteProductListCache();
        }
        return result;
    }

    @Override
    public int update(Product product) {
        int result = productMapper.update(product);
        if (result > 0 && product.getId() != null) {
            deleteProductCache(product.getId());
            deleteProductListCache();
        }
        return result;
    }

    @Override
    public int deleteById(Long id) {
        int result = productMapper.deleteById(id);
        if (result > 0) {
            deleteProductCache(id);
            deleteProductListCache();
        }
        return result;
    }
}