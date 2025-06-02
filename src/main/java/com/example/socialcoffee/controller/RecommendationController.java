package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.postgres.User;
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
    @GetMapping("/base-on-your-preference")
    public ResponseEntity<ResponseMetaData> findBasedOnYourPreferences() {
        User user = getCurrentUser();
        return recommendationService.findBasedOnYourPreferences(user);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/similar-place")
    public ResponseEntity<ResponseMetaData> findSimilarToPlacesYouLike() {
        User user = getCurrentUser();
        return recommendationService.findSimilarToPlacesYouLike(user);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/you-follow")
    public ResponseEntity<ResponseMetaData> findLikedByPeopleYouFollow() {
        User user = getCurrentUser();
        return recommendationService.findLikedByPeopleYouFollow(user);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/you-may-like")
    public ResponseEntity<ResponseMetaData> findYouMayLikeRecommendation() {
        User user = getCurrentUser();
        return recommendationService.findYouMayLikeRecommendation(user);
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
