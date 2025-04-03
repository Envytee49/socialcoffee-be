package com.example.socialcoffee.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "review_reactions")
public class ReviewReaction {
    @EmbeddedId
    private ReviewReactionId id;
    private String type;
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ReviewReactionId {
        private Long reviewId;
        private Long userId;
    }
}
