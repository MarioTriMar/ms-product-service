package com.tfm.ms_product_service.model;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "productEntity" )
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private ProductCompany company;

}
