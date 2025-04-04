package com.example.socialcoffee.dto.common;

import lombok.Data;

@Data
public class AddressDTO {
    private String googleMapUrl;
    private String addressDetail;
    private String province;
    private String district;
    private String ward;
    private double longitude;
    private double latitude;
}
