package com.example.socialcoffee.model;

import lombok.Data;

import java.util.Map;

@Data
public class UserReaction {
    private Long totalReactions;

    private Map<String, Long> reactions;

    private Map<Long, String> userReactions;

}
