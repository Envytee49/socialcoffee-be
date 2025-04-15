package com.example.socialcoffee.enums;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum ReviewReaction {
    LIKE("like"),
    LOVE("love"),
    CARE("care"),
    HAHA("haha"),
    WOW("wow"),
    SAD("sad"),
    ANGRY("angry");
    private final String value;
    ReviewReaction(String value) {
        this.value = value;
    }

    public static boolean reactionIsExist(String reaction) {
        return Arrays.stream(ReviewReaction.values()).anyMatch(item -> item.value.equalsIgnoreCase(reaction));
    }
}
