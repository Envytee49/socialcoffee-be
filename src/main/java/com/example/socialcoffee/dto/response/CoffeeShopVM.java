package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.util.Pair;

import java.util.Map;

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


}
