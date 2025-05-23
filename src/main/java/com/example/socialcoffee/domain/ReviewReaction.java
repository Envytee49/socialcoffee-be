package com.example.socialcoffee.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reactions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReviewReaction {
    @EmbeddedId
    private ReviewReactionId id;

    private String type;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ReviewReaction(ReviewReactionId reviewReactionId, String reaction) {
        this.id = reviewReactionId;
        this.type = reaction;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ReviewReactionId {
        private Long reviewId;

        private Long userId;
    }
}
