package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Review;
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

    @Query(value = "SELECT r FROM Review r " +
            "JOIN UserFollow uf ON uf.userFollowerId.followerId = r.user.id " +
            "WHERE r.privacy in :privacy AND uf.userFollowerId.followeeId = :userId")
    Page<Review> findAllByStatusAndRelation(List<String> privacy, Long userId, Pageable pageable);
}
