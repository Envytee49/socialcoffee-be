package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByCoffeeShop(CoffeeShop coffeeShop, Pageable pageable);

    Review findByIdAndCoffeeShopIdAndStatus(Long reviewId, Long shopId, String value);
}
