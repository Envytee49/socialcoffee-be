package com.example.socialcoffee.model;

public interface CoffeeShopMoodCountDTO {
    Long getShopId();

    String getShopName();

    String getCoverPhoto();

    Integer getOpenHour();

    Integer getCloseHour();

    String getAddressDetail();

    String getProvince();

    String getDistrict();

    String getWard();

    Double getLatitude();

    Double getLongitude();

    String getMood();

    Long getMoodCount();
}
