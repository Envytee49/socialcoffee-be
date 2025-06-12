package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.ReviewReaction;
import com.example.socialcoffee.domain.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, ReviewReaction.ReviewReactionId> {
    @Query(value = "SELECT rr FROM ReviewReaction rr WHERE rr.id.reviewId in (:reviewIds)")
    List<ReviewReaction> findByReviewIdIn(List<Long> reviewIds);
}
