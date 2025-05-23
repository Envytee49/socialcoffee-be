package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    READ("read"), UNREAD("unread");

    private final String value;

    NotificationStatus(final String value) {
        this.value = value;
    }
}
