package com.example.socialcoffee.model;

public class CoffeeShopRecommendationDTO {
    private Long shopId;

    private Double score;

    // Constructor
    public CoffeeShopRecommendationDTO(Long shopId, Double score) {
        this.shopId = shopId;
        this.score = score;
    }

    // Getters and Setters
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}