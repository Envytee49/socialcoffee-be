package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum AuthAction {
    LOGIN("login"), REGISTER("register");
    private final String value;

    AuthAction(final String value) {
        this.value = value;
    }
}
