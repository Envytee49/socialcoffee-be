package com.example.socialcoffee.enums;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum ReviewVote {
    UPVOTE("upvote"),
    DOWNVOTE("downvote");
    private final String value;
    ReviewVote(String value) {
        this.value = value;
    }

    public static boolean reactionIsExist(String reaction) {
        return Arrays.stream(ReviewVote.values()).anyMatch(item -> item.value.equalsIgnoreCase(reaction));
    }
}
