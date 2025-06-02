package com.example.socialcoffee.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContributionResponse {
    @NotBlank(message = "Name is required")
    private String name;

    private String coverPhotoPath;

    private List<String> galleryPhotoPaths;

    private String phoneNumber;

    private String webAddress;

    private String menuWebAddress;

    private String additionInfo;

    private Integer openHour;

    private Integer closeHour;

    private String googleMapUrl;

    private String addressDetail;

    private String province;

    private String district;

    private String ward;

    private Double longitude;

    private Double latitude;


    private List<Long> ambiances;


    private List<Long> amenities;


    private List<Long> capacities;


    private List<Long> purposes;


    private List<Long> dressCodes;


    private List<Long> entertainments;


    private List<Long> parkings;


    private List<Long> prices;


    private List<Long> serviceTypes;


    private List<Long> spaces;


    private List<Long> specialties;


    private List<Long> visitTimes;

}
