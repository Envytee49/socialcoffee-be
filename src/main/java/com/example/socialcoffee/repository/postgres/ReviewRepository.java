package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.Image;
import com.example.socialcoffee.domain.postgres.Review;
import com.example.socialcoffee.domain.postgres.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByCoffeeShop(CoffeeShop coffeeShop, Pageable pageable);

    Review findByIdAndCoffeeShopIdAndStatus(Long reviewId, Long shopId, String value);

    @Query(value = "SELECT r.rating, COUNT(r.rating) FROM Review r WHERE r.coffeeShop = :coffeeShop GROUP BY r.rating")
    List<Object[]> getReviewSummary(@Param(value = "coffeeShop") CoffeeShop coffeeShop);

    Page<Review> findAllByCoffeeShopAndStatus(CoffeeShop coffeeShop,
                                              String status,
                                              Pageable pageable);
//
//    @Query(value = "SELECT r FROM Review r " +
//            "JOIN UserFollow uf ON uf.userFollowerId.followerId = r.user.id " +
//            "WHERE r.privacy in :privacy AND uf.userFollowerId.followeeId = :userId")
//    Page<Review> findAllByStatusAndRelation(List<String> privacy, Long userId, Pageable pageable);

    Page<Review> findAllByUserAndStatus(User viewingUser, String value, Pageable pageable);

    @Query(value = "SELECT r.images FROM Review r WHERE r.user.id = :id")
    Page<Image> findPhotosByUserId(Long id, Pageable pageRequest);

    @Query(value = "SELECT r.images FROM Review r WHERE r.user.id = :id ORDER BY r.createdAt DESC LIMIT 6")
    List<Image> findPhotosByUserId(Long id);

    @Query(value = "SELECT r.coffeeShop.id, AVG(r.rating), COUNT(r) FROM Review r WHERE r.coffeeShop.id IN :shopIds GROUP BY r.coffeeShop.id")
    List<Object[]> getAverageRatingByCoffeeShopId(List<Long> shopIds);

    @Query(value = """
                SELECT r.* FROM reviews r
                LEFT JOIN review_reactions rr ON r.id = rr.review_id
                GROUP BY r.id
                ORDER BY SUM(
                    CASE rr.type
                        WHEN 'upvote' THEN 1
                        WHEN 'downvote' THEN -1
                        ELSE 0
                    END
                ) DESC
            """, nativeQuery = true)
    Page<Review> findAllOrderByScoreDesc(Pageable pageable);

    @Query(value = """
                SELECT r.* FROM reviews r
                      LEFT JOIN review_reactions rr ON r.id = rr.review_id
                      GROUP BY r.id
                      ORDER BY SUM(
                          CASE rr.type
                              WHEN 'upvote' THEN
                                  CASE
                                      WHEN rr.created_at >= CURRENT_TIMESTAMP - INTERVAL '3 days' THEN 2
                                      ELSE 1
                                  END
                              WHEN 'downvote' THEN -1
                              ELSE 0
                          END
                      ) DESC;
            """, nativeQuery = true)
    Page<Review> findAllOrderByTrending(Pageable pageable);

    Page<Review> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<Review> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Page<Review> findByStatus(String value, Pageable of);
}
