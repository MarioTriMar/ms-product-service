package com.tfm.ms_product_service.service;

import com.tfm.ms_product_service.model.*;
import com.tfm.ms_product_service.repository.ProductRepository;
import com.tfm.ms_product_service.service.restTemplate.CompanyRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyRestTemplate companyRestTemplate;

    @Caching(evict = {
            @CacheEvict(cacheNames = "products", key = "'allProducts'"),
            @CacheEvict(cacheNames = "companyProducts", key = "#product.company")
    })
    public ResponseEntity createProduct(ProductDTO product) {
        Company company;
        try{
            company = companyRestTemplate.getCompany(product.getCompany());
        }catch (Exception e){
            return new ResponseEntity("Error finding company of product", HttpStatus.NOT_FOUND);
        }

        Product productCannon = buildCannonProduct(product, company);
        productRepository.save(productCannon);
        return new ResponseEntity("Product created", HttpStatus.CREATED);

    }

    private Product buildCannonProduct(ProductDTO product, Company company) {
        Product productCannon = new Product();
        productCannon.setName(product.getName());
        productCannon.setDescription(product.getDescription());
        productCannon.setPrice(product.getPrice());
        ProductCompany productCompany = new ProductCompany();
        productCannon.setCompany(productCompany);
        productCannon.getCompany().setId(company.getId());
        productCannon.getCompany().setName(company.getName());
        productCannon.setStock(product.getStock());
        return productCannon;
    }

    @Cacheable(cacheNames = "product", key="#id", condition = "#id!=null")
    public Product getProduct(String id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()){
            return optProduct.get();
        }else {
            return null;
        }
    }

    @CachePut(cacheNames = "product", key = "#id")
    @Caching(evict = {
            @CacheEvict(cacheNames = "companyProducts", key = "#product.company"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    public ResponseEntity partialUpdateProduct(String id, ProductDTO product) {
        Product productOrg = getProduct(id);
        if(productOrg == null){
            return new ResponseEntity("Product not found", HttpStatus.NOT_FOUND);
        }
        boolean change = false;
        if(product.getPrice()>0.0){
            productOrg.setPrice(product.getPrice());
            change = true;
        }
        if(product.getStock()>0){
            productOrg.setStock(product.getStock());
            change = true;
        }
        if(product.getDescription()!=null){
            productOrg.setDescription(product.getDescription());
            change = true;
        }
        if(change){
            Product productUpdated = productRepository.save(productOrg);
            return new ResponseEntity(productUpdated, HttpStatus.OK);
        }else{
            return new ResponseEntity("Product didn't received an update", HttpStatus.OK);
        }
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "#id"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'"),
            @CacheEvict(cacheNames = "companyProducts", allEntries = true)
    })
    public ResponseEntity deleteProduct(String id) {
        Product product = getProduct(id);
        if(product==null){
            return new ResponseEntity("Product not found", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        return new ResponseEntity("Product deleted", HttpStatus.OK);
    }

    public ListProductResponse validateOrder(List<OrderProductDTO> orderProducts) {
        ListProductResponse listProductResponse = new ListProductResponse();
        List<ProductResponse> productResponses = new ArrayList<>();
        listProductResponse.setProductResponse(productResponses);
        for(OrderProductDTO orderProductDTO: orderProducts){
            ProductResponse productResponse = new ProductResponse();
            Product product = productRepository.findById(orderProductDTO.getProductId()).get();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setCompanyName(product.getCompany().getName());
            productResponse.setPrice(product.getPrice());
            productResponse.setQuantity(orderProductDTO.getQuantity());
            if(product.getStock()>=orderProductDTO.getQuantity()){
                product.setStock(product.getStock()-orderProductDTO.getQuantity());
                productResponse.setStockAllow(true);
                productRepository.save(product);
            }else{
                productResponse.setStockAllow(false);
            }

            listProductResponse.getProductResponse().add(productResponse);
        }
        return listProductResponse;
    }

    @Cacheable(cacheNames = "products", key = "'allProducts'")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(cacheNames = "companyProducts", key="#id", condition = "#id!=null")
    public List<Product> getAllCompanyProducts(String id) {
        return productRepository.findByCompany_Id(id);
    }
}
