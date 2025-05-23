package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/for-you")
    public ResponseEntity<ResponseMetaData> getRecommendationForYou() {
        User user = getCurrentUser();
        return recommendationService.getRecommendationForYou(user);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/people-with-same-taste")
    public ResponseEntity<ResponseMetaData> getPeopleWithSameTaste() {
        return recommendationService.getPeopleWithSameTaste();
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

    @GetMapping("/semantic-search")
    public ResponseEntity<ResponseMetaData> getRecommendation(@RequestParam String prompt) {
        return recommendationService.getRecommendation(prompt);
    }
}
