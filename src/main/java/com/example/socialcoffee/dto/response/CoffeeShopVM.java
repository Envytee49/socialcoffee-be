package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.utils.DateTimeUtil;
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

    public static CoffeeShopVM toVM(CoffeeShop coffeeShop) {
        return CoffeeShopVM.builder()
                .id(coffeeShop.getId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .detailAddress(coffeeShop.getAddress().getAddressDetail())
                .overviewAddress(coffeeShop.getOverviewAddress())
                .status(DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                                                            coffeeShop.getCloseHour()))
                .openHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour()))
                .closeHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour()))
                .longitude(coffeeShop.getAddress().getLongitude())
                .latitude(coffeeShop.getAddress().getLatitude())
                .name(coffeeShop.getName())
                .build();
    }

    public static CoffeeShopVM toVM(CoffeeShop coffeeShop,
                                    Map<Long, Pair<Double, Long>> reviewSummaries) {
        final Pair<Double, Long> ratingAndReviewCount = reviewSummaries.getOrDefault(coffeeShop.getId(),
                                                                       null);
        Double averageRating = ratingAndReviewCount == null ? null : NumberUtil.roundToTwoDecimals(ratingAndReviewCount.getFirst());
        Long reviewCounts = ratingAndReviewCount == null ? null : ratingAndReviewCount.getSecond();

        return CoffeeShopVM.builder()
                .id(coffeeShop.getId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .detailAddress(coffeeShop.getAddress().getAddressDetail())
                .overviewAddress(coffeeShop.getOverviewAddress())
                .status(DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                                                            coffeeShop.getCloseHour()))
                .openHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour()))
                .closeHour(DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour()))
                .longitude(coffeeShop.getAddress().getLongitude())
                .latitude(coffeeShop.getAddress().getLatitude())
                .averageRating(averageRating)
                .reviewCounts(reviewCounts)
                .name(coffeeShop.getName())
                .build();
    }


}
