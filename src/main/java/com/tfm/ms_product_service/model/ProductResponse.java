package com.tfm.ms_product_service.model;

import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String companyName;
    private double price;
    private int quantity;
    private boolean stockAllow;
}
