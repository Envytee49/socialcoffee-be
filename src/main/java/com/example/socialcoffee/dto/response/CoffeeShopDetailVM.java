package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.model.Address;
import com.example.socialcoffee.model.Image;
import com.example.socialcoffee.model.feature.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CoffeeShopDetailVM {
    private Long id;
    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String additionInfo;
    private Integer openHour;
    private Integer closeHour;
    private List<Image> galleryPhotos;
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
    private List<ServiceType> serviceTypes;
    private List<Space> spaces;
    private List<Specialty> specialties;
    private List<VisitTime> visitTimes;
}
