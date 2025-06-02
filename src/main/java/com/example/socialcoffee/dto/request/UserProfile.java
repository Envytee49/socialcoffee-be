package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.utils.DateTimeUtil;
import lombok.Getter;

@Getter
public class UserProfile {
    private Long userId;

    private String profileUrl;

    private String backgroundUrl;

    private String bio;

    private String username;

    private String displayName;

    private String phone;

    private String email;

    private String gender;

    private String dob;

    private String YYYYMMDDdob;

    private String location;

    private String joined;

    private Boolean isFollowing;

    public UserProfile(User user,
                       boolean isFollowing) {
        this(user);
        this.isFollowing = isFollowing;
    }

    public UserProfile(User user) {
        this.userId = user.getId();
        this.profileUrl = user.getProfilePhoto();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.joined = DateTimeUtil.covertLocalDateToString(user.getCreatedAt());
        this.location = "Hanoi, Vietnam";
        this.phone = user.getPhone();
        this.backgroundUrl = user.getBackgroundPhoto();
        this.YYYYMMDDdob = DateTimeUtil.covertLocalDateToYYYYMMDDString(user.getDob());
        this.dob = DateTimeUtil.covertLocalDateToString(user.getDob());
        this.gender = user.getGender();
    }
}
