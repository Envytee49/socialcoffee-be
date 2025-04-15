package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Privacy {
    PUBLIC("public"),
    PRIVATE("private"),
    FOLLOWER("follower");
    private final String value;

    Privacy(String value) {
        this.value = value;
    }
}
