package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum AuthProviderEnum {
    GOOGLE("google"), FACEBOOK("facebook");

    private final String value;

    AuthProviderEnum(final String value) {
        this.value = value;
    }
}
