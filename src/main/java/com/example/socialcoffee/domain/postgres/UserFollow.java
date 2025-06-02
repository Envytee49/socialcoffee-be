package com.example.socialcoffee.domain.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
@Data
public class UserFollow {

    @EmbeddedId
    private UserFollowerId userFollowerId;

    @CreationTimestamp
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

