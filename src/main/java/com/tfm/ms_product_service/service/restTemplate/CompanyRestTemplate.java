package com.tfm.ms_product_service.service.restTemplate;

import com.tfm.ms_product_service.controller.ProductController;
import com.tfm.ms_product_service.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${api.url.ms-company-service}")
    private String msCompanyServiceUrl;

    private Logger logger= LoggerFactory.getLogger(CompanyRestTemplate.class);


    public Company getCompany(String companyId) throws Exception {
        String url = msCompanyServiceUrl + "/company/" + companyId;
        logger.info("Call url: {}", url);
        ResponseEntity<Company> responseEntity = restTemplate.getForEntity(url, Company.class, companyId);

        if (responseEntity.getStatusCode().is2xxSuccessful()){
            logger.info("Company OK");
            return responseEntity.getBody();
        }else{
            throw new Exception("Company does not exist");
        }

    }
}
