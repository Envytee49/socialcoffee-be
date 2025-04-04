package com.example.socialcoffee.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class RestConfiguration {
    @Bean
    public HttpHeaders defaultHeaders(OllamaConfig ollamaConfig) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ollamaConfig.getApiKey());
        return headers;
    }
}
