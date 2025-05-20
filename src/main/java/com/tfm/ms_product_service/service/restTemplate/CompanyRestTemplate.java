package com.tfm.ms_product_service.service.restTemplate;

import com.tfm.ms_product_service.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CompanyRestTemplate {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url.ms-company-service}")
    private String msCompanyServiceUrl;

    public Company getCompany(String companyId) throws Exception {
        String url = msCompanyServiceUrl + "/company/" + companyId;
        ResponseEntity<Company> responseEntity = restTemplate.getForEntity(url, Company.class, companyId);

        if (responseEntity.getStatusCode().is2xxSuccessful()){
            return responseEntity.getBody();
        }else{
            throw new Exception("Company does not exist");
        }

    }
}
