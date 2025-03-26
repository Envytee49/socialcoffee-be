package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum CoffeeShopSort {
    HIGHEST_RATED("Highest Rated"),
    MOST_REVIEW("Most Review"),;
    private final String value;

    CoffeeShopSort(String value) {
        this.value = value;
    }
}
