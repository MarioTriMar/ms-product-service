package com.tfm.ms_product_service.model;

import lombok.Data;

@Data
public class OrderProductDTO {
    private String productId;
    private int quantity;
}
