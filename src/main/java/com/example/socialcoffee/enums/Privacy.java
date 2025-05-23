package com.example.socialcoffee.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Privacy {
    PUBLIC("public"),
    PRIVATE("private"),
    FOLLOWER("follower");

    private final String value;

    Privacy(String value) {
        this.value = value;
    }

    public static boolean privacyExist(String value) {
        return Arrays.stream(Privacy.values()).anyMatch(privacy -> privacy.getValue().equalsIgnoreCase(value));
    }
}
