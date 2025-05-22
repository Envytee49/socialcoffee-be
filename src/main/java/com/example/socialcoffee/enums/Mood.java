package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Mood {
    HAPPY("happy"),
    SAD("sad"),
    SOCIAL("social"),
    RELAXED("relaxed"),
    STRESSED("stressed");
    private final String value;

    Mood(final String value) {
        this.value = value;
    }
}
