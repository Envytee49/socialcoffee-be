package com.example.socialcoffee.dto.request;

import lombok.Getter;

@Getter
public class UserProfile {
    private String profileUrl;
    private String username;
    public UserProfile(String profileUrl, String username) {
        this.profileUrl = profileUrl;
        this.username = username;
    }
}
