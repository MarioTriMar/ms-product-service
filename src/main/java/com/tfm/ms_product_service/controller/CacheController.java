package com.tfm.ms_product_service.controller;

import com.tfm.ms_product_service.config.CacheEntries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("cache")
public class CacheController {

    @Autowired
    private CacheEntries cacheEntries;

    @GetMapping("/{cacheName}")
    public Map<Object, Object> getCompanyById(@PathVariable String cacheName){
        return this.cacheEntries.getAllEntriesInProductCache(cacheName);
    }


}
