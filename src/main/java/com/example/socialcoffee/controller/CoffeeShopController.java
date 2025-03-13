package com.example.socialcoffee.controller;

import com.example.socialcoffee.configuration.ConfigResource;
import com.example.socialcoffee.dto.request.GenerateRequest;
import com.example.socialcoffee.dto.response.GenerateResponse;
import com.example.socialcoffee.model.CoffeeShop;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coffee-shop")
public class CoffeeShopController {
    private final RestTemplate restTemplate;
    private final ConfigResource configResource;

    public CoffeeShopController(final RestTemplate restTemplate,
                                final ConfigResource configResource) {
        this.restTemplate = restTemplate;
        this.configResource = configResource;
    }

    @PostMapping
    public CoffeeShop createCoffeeShop() {
        Map<String, List<String>> features = new HashMap<>();
        features.put("name", List.of("Cheese Coffee"));
        features.put("space", List.of("garden"));
        features.put("capacity", List.of("3-5 people"));
        features.put("service", List.of("good"));
        features.put("ambiance", List.of("fancy"));
        features.put("amenity", List.of("wheelchair available"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configResource.getApiKey());
        GenerateRequest request = GenerateRequest.getDefaultMessage(configResource, "Generate description for a coffee shop with these features (limit 150 wors): " + features.toString());
        String embeddingUrl = configResource.getEmbeddingUrl() + configResource.getEmbeddingModel();
        String generatingUrl = configResource.getGeneratingUrl() + configResource.getGeneratingModel();
        ResponseEntity<GenerateResponse> generatedDescription = restTemplate.exchange(generatingUrl,
                                                                          HttpMethod.POST,
                                                                          new HttpEntity<>(request,
                                                                                           headers),
                                                                          GenerateResponse.class);
        GenerateResponse body = generatedDescription.getBody();
        return new CoffeeShop(1L,
                              body.getChoices().getFirst().getMessage().getContent(),
                              null);
    }
}
