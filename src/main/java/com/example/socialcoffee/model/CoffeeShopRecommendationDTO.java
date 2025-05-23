package com.example.socialcoffee.model;

public class CoffeeShopRecommendationDTO {
    private Long shopId;

    private String name;

    private String coverPhoto;

    private String status;

    private Double score;

    private String matchReason;

    private Double avgRating; // New field for average rating

    // Constructor
    public CoffeeShopRecommendationDTO(Long shopId, String name, String coverPhoto, String status, Double score, String matchReason, Double avgRating) {
        this.shopId = shopId;
        this.name = name;
        this.coverPhoto = coverPhoto;
        this.status = status;
        this.score = score;
        this.matchReason = matchReason;
        this.avgRating = avgRating;
    }

    // Getters and Setters
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}