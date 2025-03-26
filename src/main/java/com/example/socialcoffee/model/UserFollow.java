package com.example.socialcoffee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_follows",
        indexes = {
                @Index(name = "idx_follower_id", columnList = "followerId"),
                @Index(name = "idx_followee_id", columnList = "followeeId")
        }
)
public class UserFollow {

    @EmbeddedId
    private UserFollowerId userFollowerId;

    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserFollowerId implements Serializable {
        private Long followerId;
        private Long followeeId;
    }
}

