package com.example.socialcoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public interface CoffeeShopMoodCountDTO {
    Long getShopId();

    String getShopName();

    String getCoverPhoto();

    String getPhoneNumber();

    String getWebAddress();

    Integer getOpenHour();

    Integer getCloseHour();

    String getAddressDetail();

    Double getLatitude();

    Double getLongitude();

    String getMood();

    Long getMoodCount();

    Double getAverageRating();

    Long getReviewCount();

    @JsonIgnore
    default String getOverviewAddress() {
        List<String> parts = new ArrayList<>();

        if (StringUtils.isNotBlank(getProvince())) {
            parts.add(getProvince());
        }
        if (StringUtils.isNotBlank(getDistrict())) {
            parts.add(getDistrict());
        }
        if (StringUtils.isNotBlank(getWard())) {
            parts.add(getWard());
        }

        return String.join(", ", parts);
    }

    String getProvince();

    String getDistrict();

    String getWard();

}
