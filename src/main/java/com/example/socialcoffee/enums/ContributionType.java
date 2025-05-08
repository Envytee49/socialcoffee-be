package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum ContributionType {
    SUGGESTED("Suggested"),
    CONTRIBUTED("Contributed");
    private final String value;

    ContributionType(String value) {
        this.value = value;
    }
}
