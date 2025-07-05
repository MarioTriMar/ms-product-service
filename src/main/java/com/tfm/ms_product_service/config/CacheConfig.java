package com.tfm.ms_product_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tfm.ms_product_service.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PRODUCT = "product";
    public static final String PRODUCTS = "products";
    public static final String COMPANY_PRODUCTS = "companyProducts";
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;

    @Bean
    public LettuceConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        return new LettuceConnectionFactory(configuration);
    }
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration(PRODUCT,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class))))
                .withCacheConfiguration(PRODUCTS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(COMPANY_PRODUCTS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .build();
    }

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES);
    }
    @Bean
    @Primary
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(List.of(PRODUCT, PRODUCTS, COMPANY_PRODUCTS));
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
