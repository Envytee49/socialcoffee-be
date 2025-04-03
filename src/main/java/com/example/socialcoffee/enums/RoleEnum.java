package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN("admin"),
    USER("user");
    private final String value;

    RoleEnum(final String value) {
        this.value = value;
    }
}
