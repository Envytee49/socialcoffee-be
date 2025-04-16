package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.domain.User;
import lombok.Getter;

@Getter
public class UserProfile {
    private String profileUrl;
    private String backgroundUrl;
    private String bio;
    private String username;
    private String displayName;
    private String email;
    public UserProfile(User user) {
        this.profileUrl = user.getProfilePhoto();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.backgroundUrl = user.getBackgroundPhoto();
    }
}
