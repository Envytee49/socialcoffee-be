package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController extends BaseController {
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

    @GetMapping("/top-10-of-all-time")
    public ResponseEntity<ResponseMetaData> getTop1OfAllTime() {
        return recommendationService.getTop1OfAllTime();
    }

    @GetMapping("/trending-this-week")
    public ResponseEntity<ResponseMetaData> getTrendingThisWeek() {
        return recommendationService.getTrendingThisWeek();
    }

    @GetMapping("/trending-this-month")
    public ResponseEntity<ResponseMetaData> getTrendingThisMonth() {
        return recommendationService.getTrendingThisMonth();
    }
}
