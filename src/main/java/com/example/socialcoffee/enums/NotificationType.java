package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    COFFEE_SHOP("coffee_shop"),
    USER("user");

    private final String value;

    NotificationType(final String value) {
        this.value = value;
    }
}
