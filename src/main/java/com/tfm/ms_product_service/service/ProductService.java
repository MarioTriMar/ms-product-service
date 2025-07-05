package com.tfm.ms_product_service.service;

import com.tfm.ms_product_service.model.*;
import com.tfm.ms_product_service.repository.ProductRepository;
import com.tfm.ms_product_service.service.redis.ProductRedisService;
import com.tfm.ms_product_service.service.restTemplate.CompanyRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRedisService productRedisService;

    private Logger logger= LoggerFactory.getLogger(ProductService.class);

    @Caching(evict = {
            @CacheEvict(cacheNames = "products", key = "'allProducts'"),
            @CacheEvict(cacheNames = "companyProducts", key = "#product.company")
    })
    public ResponseEntity createProduct(ProductDTO product) {
        return this.productRedisService.createProduct(product);

    }


    @Cacheable(cacheNames = "product", key="#id", condition = "#id!=null")
    public Product getProduct(String id) {
        logger.info("Product not in local cache, searching in Redis");
        return this.productRedisService.getProduct(id);
    }

    @CachePut(cacheNames = "product", key = "#id")
    @Caching(evict = {
            @CacheEvict(cacheNames = "companyProducts", key = "#product.company"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    public ResponseEntity partialUpdateProduct(String id, ProductDTO product) {
        return this.productRedisService.partialUpdateProduct(id,product);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "#id"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'"),
            @CacheEvict(cacheNames = "companyProducts", allEntries = true)
    })
    public ResponseEntity deleteProduct(String id) {
        return this.productRedisService.deleteProduct(id);
    }

    public ListProductResponse validateOrder(List<OrderProductDTO> orderProducts) {
        return this.productRedisService.validateOrder(orderProducts);
    }

    @Cacheable(cacheNames = "products", key = "'allProducts'")
    public List<Product> getAllProducts() {
        logger.info("Products not in local cache, searching in Redis");
        return this.productRedisService.getAllProducts();
    }

    @Cacheable(cacheNames = "companyProducts", key="#id", condition = "#id!=null")
    public List<Product> getAllCompanyProducts(String id) {
        logger.info("Company products not in local cache, searching in Redis");
        return this.productRedisService.getAllCompanyProducts(id);
    }
}
