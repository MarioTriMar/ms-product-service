package com.tfm.ms_product_service.model;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String description;
    @Min(0)
    private double price;
    @Min(0)
    private int stock;
    private String company;
}
