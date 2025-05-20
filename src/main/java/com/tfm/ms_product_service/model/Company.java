package com.tfm.ms_product_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Company {

    private String id;
    private String name;
    private String address;
    private String supportPhone;
    private String email;
    private String cif;
}

