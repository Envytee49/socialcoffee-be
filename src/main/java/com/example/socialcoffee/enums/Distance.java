package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Distance {
    DRIVING("10km"),
    BIKING("5km"),
    WALKING("2km");
    private final String value;

    Distance(String value) {
        this.value = value;
    }

}
