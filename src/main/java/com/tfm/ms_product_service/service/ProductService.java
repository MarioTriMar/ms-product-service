package com.tfm.ms_product_service.service;

import com.tfm.ms_product_service.model.*;
import com.tfm.ms_product_service.repository.ProductRepository;
import com.tfm.ms_product_service.service.restTemplate.CompanyRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity getProduct(String id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()){
            return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity partialUpdateProduct(String id, ProductDTO product) {
        ResponseEntity response = getProduct(id);
        if(response.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity("Product not found", HttpStatus.NOT_FOUND);
        }
        Product productCannon = (Product) response.getBody();
        boolean change = false;
        if(product.getPrice()>0.0){
            productCannon.setPrice(product.getPrice());
            change = true;
        }
        if(product.getStock()>0){
            productCannon.setStock(product.getStock());
            change = true;
        }
        if(product.getDescription()!=null){
            productCannon.setDescription(product.getDescription());
            change = true;
        }
        if(change){
            Product productUpdated = productRepository.save(productCannon);
            return new ResponseEntity(productUpdated, HttpStatus.OK);
        }else{
            return new ResponseEntity("Product didnt received an update", HttpStatus.OK);
        }
    }

    public ResponseEntity deleteProduct(String id) {
        ResponseEntity response = getProduct(id);
        if(response.getStatusCode() != HttpStatus.OK){
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
            Product product = new Product();
            product = productRepository.findById(orderProductDTO.getProductId()).get();
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
}
