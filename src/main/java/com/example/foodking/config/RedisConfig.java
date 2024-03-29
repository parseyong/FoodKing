package com.example.foodking.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    @Primary
    public RedissonClient authNumberRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+port).setDatabase(1);
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("isAuthNumberRedis")
    public RedissonClient isAuthNumberRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+port).setDatabase(2);
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("tokenRedis")
    public RedissonClient tokenRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+port).setDatabase(3);
        return Redisson.create(config);
    }
}