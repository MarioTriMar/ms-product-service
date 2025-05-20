package com.tfm.ms_product_service.model;

import lombok.Data;

import java.util.List;

@Data
public class ListProductResponse {
    private List<ProductResponse> productResponse;
}
