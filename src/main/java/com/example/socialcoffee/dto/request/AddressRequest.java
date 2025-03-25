package com.example.socialcoffee.dto.request;

import lombok.Getter;

@Getter
public class AddressRequest {
    private String googleMapUrl;
    private String province;
    private String district;
    private String ward;
    private double longitude;
    private double latitude;
}
