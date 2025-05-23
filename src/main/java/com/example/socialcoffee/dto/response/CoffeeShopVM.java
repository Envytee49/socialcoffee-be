package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.model.CoffeeShopMoodCountDTO;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoffeeShopVM {
    private Long id;
    private String coverPhoto;
    private String name;
    private String detailAddress;
    private String overviewAddress;
    private String status;
    private String openHour;
    private String closeHour;
    private Double averageRating;
    private Long reviewCounts;
    @JsonProperty("lng")
    private double longitude;
    @JsonProperty("lat")
    private double latitude;
    private Double distance;
    @JsonProperty("is_sponsored")
    private Boolean isSponsored;
    private String mood;
    private Long moodCount;
    private Double score;
    public static CoffeeShopVM toVM(CoffeeShop coffeeShop, Double userLat, Double userLng, Double score) {
        Boolean isFalseLocation= coffeeShop.getAddress().getLatitude() > coffeeShop.getAddress().getLongitude();
        Double longitude = isFalseLocation ? coffeeShop.getAddress().getLatitude() : coffeeShop.getAddress().getLongitude();
        Double latitude = isFalseLocation ? coffeeShop.getAddress().getLongitude() : coffeeShop.getAddress().getLatitude();

        return CoffeeShopVM.builder()
                .id(coffeeShop.getId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .detailAddress(coffeeShop.getAddress().getAddressDetail())
                .overviewAddress(coffeeShop.getOverviewAddress())
                .status(DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                                                            coffeeShop.getCloseHour()))
                .score(NumberUtil.roundToTwoDecimals(score * 100))
                .openHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour()))
                .closeHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour()))
                .longitude(longitude)
                .isSponsored(coffeeShop.getIsSponsored())
                .latitude(latitude)
                .distance(GeometryUtil.calculateDistance(userLat, userLng, latitude, longitude))
                .averageRating(NumberUtil.roundToTwoDecimals(coffeeShop.getAverageRating()))
                .reviewCounts(coffeeShop.getReviewCount())
                .name(coffeeShop.getName())
                .build();
    }

    public static CoffeeShopVM toVM(CoffeeShop coffeeShop, Double userLat, Double userLng) {
        Boolean isFalseLocation= coffeeShop.getAddress().getLatitude() > coffeeShop.getAddress().getLongitude();
        Double longitude = isFalseLocation ? coffeeShop.getAddress().getLatitude() : coffeeShop.getAddress().getLongitude();
        Double latitude = isFalseLocation ? coffeeShop.getAddress().getLongitude() : coffeeShop.getAddress().getLatitude();

        return CoffeeShopVM.builder()
                .id(coffeeShop.getId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .detailAddress(coffeeShop.getAddress().getAddressDetail())
                .overviewAddress(coffeeShop.getOverviewAddress())
                .status(DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                        coffeeShop.getCloseHour()))
                .openHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour()))
                .closeHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour()))
                .longitude(longitude)
                .isSponsored(coffeeShop.getIsSponsored())
                .latitude(latitude)
                .distance(GeometryUtil.calculateDistance(userLat, userLng, latitude, longitude))
                .averageRating(NumberUtil.roundToTwoDecimals(coffeeShop.getAverageRating()))
                .reviewCounts(coffeeShop.getReviewCount())
                .name(coffeeShop.getName())
                .build();
    }

    public static CoffeeShopVM toVM(CoffeeShopMoodCountDTO coffeeShop, Double userLat, Double userLng) {
//        Boolean isFalseLocation= coffeeShop.getLatitude() > coffeeShop.getLongitude();
//        Double longitude = isFalseLocation ? coffeeShop.getLatitude() : coffeeShop.getLongitude();
//        Double latitude = isFalseLocation ? coffeeShop.getLongitude() : coffeeShop.getLatitude();

        return CoffeeShopVM.builder()
                .id(coffeeShop.getShopId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .detailAddress(coffeeShop.getAddressDetail())
//                .overviewAddress(coffeeShop.getOverviewAddress())
                .status(DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                        coffeeShop.getCloseHour()))
                .openHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour()))
                .closeHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour()))
                .longitude(coffeeShop.getLongitude())
                .latitude(coffeeShop.getLatitude())
                .distance(GeometryUtil.calculateDistance(userLat, userLng, coffeeShop.getLatitude(), coffeeShop.getLongitude()))
//                .averageRating(NumberUtil.roundToTwoDecimals(coffeeShop.getAverageRating()))
//                .reviewCounts(coffeeShop.getReviewCount())
                .name(coffeeShop.getShopName())
                .mood(coffeeShop.getMood())
                .moodCount(coffeeShop.getMoodCount())
                .build();
    }


}
