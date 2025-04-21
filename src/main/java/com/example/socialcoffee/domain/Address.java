package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "address")
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String googleMapUrl;
    private String addressDetail;
    private String province;
    private String district;
    private String ward;
    private double longitude;
    private double latitude;

    @Override
    public String toString() {
        return "Address{" +
                "googleMapUrl='" + googleMapUrl + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    public Address(final String googleMapUrl,
                   final String province,
                   final String district,
                   final String ward,
                   final double longitude,
                   final double latitude) {
        this.googleMapUrl = googleMapUrl;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
