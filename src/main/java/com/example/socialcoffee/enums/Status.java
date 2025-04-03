package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive"),
    APPROVED("approved"),
    REJECTED("rejected"),
    REPORTED("reported");
    private final String value;

    Status(final String value) {
        this.value = value;
    }
}
