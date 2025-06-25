package com.tfm.ms_product_service.model;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(value = "productEntity" )
public class Product implements Serializable {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private ProductCompany company;

}
