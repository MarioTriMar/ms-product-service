package com.tfm.ms_product_service;

import com.tfm.ms_product_service.model.*;
import com.tfm.ms_product_service.repository.ProductRepository;
import com.tfm.ms_product_service.service.ProductService;
import com.tfm.ms_product_service.service.restTemplate.CompanyRestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CompanyRestTemplate companyRestTemplate;

    @Test
    void shouldReturnNotFoundWhenCompanyNotFound() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setCompany("123");

        when(companyRestTemplate.getCompany("123")).thenThrow(new RuntimeException());

        ResponseEntity<?> response = productService.createProduct(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error finding company of product", response.getBody());
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Test");
        dto.setCompany("1");
        dto.setPrice(10.0);
        dto.setDescription("desc");
        dto.setStock(5);

        Company company = new Company();
        company.setId("1");
        company.setName("Company");

        when(companyRestTemplate.getCompany("1")).thenReturn(company);

        ResponseEntity<?> response = productService.createProduct(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product created", response.getBody());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldReturnProductWhenFound() {
        Product product = new Product();
        product.setId("1");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Product result = productService.getProduct("1");

        assertEquals(product, result);
    }

    @Test
    void shouldReturnNullWhenProductNotFound() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        Product result = productService.getProduct("1");

        assertNull(result);
    }

    @Test
    void shouldReturnNotFoundIfProductToUpdateDoesNotExist() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = productService.partialUpdateProduct("1", new ProductDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }
    @Test
    void shouldReturnOkIfNoChangesMade() {
        Product product = new Product();
        product.setId("1");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        ResponseEntity<?> response = productService.partialUpdateProduct("1", new ProductDTO());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product didn't received an update", response.getBody());
    }
    @Test
    void shouldUpdateProductWithChanges() {
        Product product = new Product();
        product.setId("1");

        ProductDTO dto = new ProductDTO();
        dto.setPrice(50.0);
        dto.setStock(10);
        dto.setDescription("Updated");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        ResponseEntity<?> response = productService.partialUpdateProduct("1", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }
    @Test
    void shouldDeleteProductWhenFound() {
        Product product = new Product();
        product.setId("1");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        ResponseEntity<?> response = productService.deleteProduct("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted", response.getBody());
        verify(productRepository).deleteById("1");
    }

    @Test
    void shouldReturnNotFoundIfProductToDeleteDoesNotExist() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = productService.deleteProduct("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }
    @Test
    void shouldValidateOrderWithStock() {
        OrderProductDTO dto = new OrderProductDTO();
        dto.setProductId("1");
        dto.setQuantity(2);

        Product product = new Product();
        product.setId("1");
        product.setName("Test");
        product.setStock(10);
        product.setPrice(5.0);
        ProductCompany company = new ProductCompany();
        company.setName("Company");
        product.setCompany(company);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        ListProductResponse response = productService.validateOrder(List.of(dto));

        assertEquals(1, response.getProductResponse().size());
        assertTrue(response.getProductResponse().get(0).isStockAllow());
        verify(productRepository).save(any());
    }
    @Test
    void shouldReturnAllProducts() {
        List<Product> products = List.of(new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(products, result);
    }

    @Test
    void shouldReturnCompanyProductsIfCompanyExists() throws Exception {
        Company company = new Company();
        company.setId("1");

        when(companyRestTemplate.getCompany("1")).thenReturn(company);
        when(productRepository.findByCompany_Id("1")).thenReturn(List.of(new Product()));

        List<Product> result = productService.getAllCompanyProducts("1");

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnNullIfCompanyNotFoundInCompanyProducts() throws Exception {
        when(companyRestTemplate.getCompany("1")).thenThrow(new RuntimeException());

        List<Product> result = productService.getAllCompanyProducts("1");

        assertNull(result);
    }



}
