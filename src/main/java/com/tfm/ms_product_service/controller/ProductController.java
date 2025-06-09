package com.tfm.ms_product_service.controller;

import com.tfm.ms_product_service.model.ListProductResponse;
import com.tfm.ms_product_service.model.OrderProductDTO;
import com.tfm.ms_product_service.model.Product;
import com.tfm.ms_product_service.model.ProductDTO;
import com.tfm.ms_product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("product")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping()
    public ResponseEntity createProduct(@RequestBody ProductDTO product) {
        if(!isValid(product)){
            return new ResponseEntity<>("Invalid product", HttpStatus.BAD_REQUEST);
        }
        return productService.createProduct(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity getProduct(@PathVariable String id) {
        if(id==null || id.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Product product = productService.getProduct(id);
        if(product==null){
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(product, HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity getAllProductsOfCompany(@PathVariable String id) {
        if(id==null || id.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productService.getAllCompanyProducts(id),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity partialUpdateProduct(@PathVariable String id, @RequestBody ProductDTO product) {
        return productService.partialUpdateProduct(id,product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id) {
        if(id==null || id.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return productService.deleteProduct(id);
    }

    @PostMapping("/order")
    public ListProductResponse validateOrder(@RequestBody List<OrderProductDTO> orderProducts){
        return productService.validateOrder(orderProducts);
    }
    private boolean isValid(ProductDTO product){
        if(product.getName()==null
        || product.getCompany()==null){
            return false;
        }
        return true;
    }
}
