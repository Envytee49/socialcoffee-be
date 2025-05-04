package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.ReviewReaction;
import com.example.socialcoffee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, ReviewReaction.ReviewReactionId> {
    @Query(value = "SELECT rr FROM ReviewReaction rr WHERE rr.id.reviewId in (:reviewIds)")
    List<ReviewReaction> findByReviewIdIn(List<Long> reviewIds);

    @Query(value = "SELECT u FROM User u WHERE u.id IN (SELECT rr.id.userId FROM ReviewReaction rr WHERE rr.id.reviewId = :reviewId AND rr.type = :type)")
    List<User> findByReviewIdAndType(Long reviewId,
                                     String type);
}
