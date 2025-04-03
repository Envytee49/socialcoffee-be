package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.CoffeeShop;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoffeeShopVM {
    private Long id;
    private String coverPhoto;
    private String name;
    @JsonProperty("lng")
    private double longitude;
    @JsonProperty("lat")
    private double latitude;
    public static CoffeeShopVM toVM(CoffeeShop coffeeShop) {
        return CoffeeShopVM.builder()
                .id(coffeeShop.getId())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .longitude(coffeeShop.getAddress().getLongitude())
                .latitude( coffeeShop.getAddress().getLatitude())
                .name(coffeeShop.getName())
                .build();
    }


}
