package com.example.socialcoffee.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CoffeeShopSearchRequest {
    private String name;
    private String province;
    private String district;
    private String ward;
    private List<String> distances;
    private List<Long> ambiances;
    private List<Long> amenities;
    private List<Long> capacities;
    private List<Long> categories;
    private List<Long> entertainments;
    private List<Long> parkings;
    private List<Long> prices;
    private List<Long> purposes;
    private List<Long> serviceTypes;
    private List<Long> spaces;
    private List<Long> specialties;
    private List<Long> visitTimes;
}

