package com.example.socialcoffee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContributionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String coverPhotoPath;

    private List<String> galleryPhotoPaths;

    private String phoneNumber;

//    @NotBlank(message = "Description is required")
//    private String description;

    private String webAddress;

    private String menuWebAddress;

    private String additionInfo;

    @NotNull(message = "Open hour is required")
    private Integer openHour;

    @NotNull(message = "Close hour is required")
    private Integer closeHour;

    private String googleMapUrl;

    private String addressDetail;

    private String province;

    private String district;

    private String ward;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotEmpty(message = "At least one ambiance must be selected")
    private List<Long> ambiances;

    @NotEmpty(message = "At least one amenity must be selected")
    private List<Long> amenities;

    @NotEmpty(message = "At least one capacity must be selected")
    private List<Long> capacities;

    private List<Long> purposes;

    private List<Long> dressCodes;

    @NotEmpty(message = "At least one entertainment option must be selected")
    private List<Long> entertainments;

    @NotEmpty(message = "At least one parking option must be selected")
    private List<Long> parkings;

    @NotEmpty(message = "At least one price option must be selected")
    private List<Long> prices;

    @NotEmpty(message = "At least one service type must be selected")
    private List<Long> serviceTypes;

    @NotEmpty(message = "At least one space type must be selected")
    private List<Long> spaces;

    private List<Long> specialties;

    private List<Long> visitTimes;

    public void addNewGalleryPhotos(List<String> imgs) {
        if (CollectionUtils.isEmpty(this.galleryPhotoPaths)) {
            this.galleryPhotoPaths = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(imgs)) return;
        this.galleryPhotoPaths.addAll(imgs);
    }
}
