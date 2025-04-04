package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.dto.common.AddressDTO;
import com.example.socialcoffee.dto.common.ImageDTO;
import lombok.Data;

import java.util.List;

@Data
public class CoffeeShopDTO {
    private Long id;
    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String description;
    private Integer openHour;
    private Integer closeHour;
    private String status;
    private AddressDTO address;
    private Long createdBy;
    private List<ImageDTO> galleryPhotos;
}
