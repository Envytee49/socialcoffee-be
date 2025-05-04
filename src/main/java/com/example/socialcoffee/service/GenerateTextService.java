package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.OllamaConfig;
import com.example.socialcoffee.dto.request.GenerateRequest;
import com.example.socialcoffee.dto.response.GenerateResponse;
import com.example.socialcoffee.utils.StringAppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateTextService {
    private final RestTemplate restTemplate;
    private final OllamaConfig ollamaConfig;
    private final HttpHeaders defaultHeaders;
    public String generateCoffeeShopDescription(String features, String prompt) {
        try {
            if (StringAppUtils.isEmpty(features)) return null;
            log.info("Start generating description");
            GenerateRequest request = GenerateRequest
                    .getDefaultMessage(ollamaConfig,prompt
                            + features);
            String generatingUrl = ollamaConfig.getGeneratingUrl();
            ResponseEntity<GenerateResponse> generatedResponse = restTemplate.exchange(generatingUrl,
                                                                                       HttpMethod.POST,
                                                                                       new HttpEntity<>(request,
                                                                                                        defaultHeaders),
                                                                                       GenerateResponse.class);
            GenerateResponse body = generatedResponse.getBody();
            log.info("Finish generating description");
            return body.getChoices().getFirst().getMessage().getContent();
        } catch (Exception e) {
            log.error("FAILED generating description", e);
            throw new RuntimeException("FAILED generating description", e);
        }
    }
}
