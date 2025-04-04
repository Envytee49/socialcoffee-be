package com.example.socialcoffee.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RedisConfig {
    @Value("${redis.prefix}")
    private String redisPrefix;
}
