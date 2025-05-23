package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Distance {
    DRIVING(10),
    BIKING(5),
    WALKING(2);

    private final Integer value;

    Distance(Integer value) {
        this.value = value;
    }

}
