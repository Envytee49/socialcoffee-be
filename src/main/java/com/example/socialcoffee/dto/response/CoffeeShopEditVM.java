package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.postgres.Address;
import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.utils.DateTimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoffeeShopEditVM {
    private Long id;

    private String name;

    private String coverPhoto;

    private String phoneNumber;

    private String webAddress;

    private String menuWebAddress;

    private String additionInfo;

    private String open;

    private String close;

    private Address address;

    private String description;

    private List<Ambiance> ambiances;

    private List<Amenity> amenities;

    private List<Capacity> capacities;

    private List<Category> categories;

    private List<DressCode> dressCodes;

    private List<Entertainment> entertainments;

    private List<Parking> parkings;

    private List<Price> prices;

    private List<Purpose> purposes;

    private List<ServiceType> serviceTypes;

    private List<Space> spaces;

    private List<Specialty> specialties;

    private List<VisitTime> visitTimes;

    public void setOpenTime(Integer openHour) {
        this.open = DateTimeUtil.convertIntegerToString(openHour);
    }

    public void setCloseTime(Integer closeHour) {
        this.close = DateTimeUtil.convertIntegerToString(closeHour);
    }
}
