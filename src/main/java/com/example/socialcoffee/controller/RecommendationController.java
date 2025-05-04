package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController extends BaseController{
    private final RecommendationService recommendationService;
    @GetMapping("/coffee-shops/{id}/related")
    public ResponseEntity<ResponseMetaData> getRelatedCoffeeShops(@PathVariable Long id) {
        return recommendationService.getRelatedCoffeeShop(id);
    }

    @GetMapping("/for-you")
    public ResponseEntity<ResponseMetaData> getRecommendationForYou() {
        User user = getCurrentUser();
        return recommendationService.getRecommendationForYou(user);
    }
}
