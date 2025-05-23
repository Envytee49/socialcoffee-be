package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    RoleEnum(final String value) {
        this.value = value;
    }
}
