package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.CoffeeShopMood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeShopMoodRepository extends JpaRepository<CoffeeShopMood, Long> {
    CoffeeShopMood findByShopIdAndUserIdAndMood(Long shopId, Long userId, String mood);
    List<CoffeeShopMood> findByShopIdAndUserId(Long shopId, Long userId);
    void deleteByShopIdAndUserId(Long shopId, Long userId);

    List<CoffeeShopMood> findByShopId(Long shopId);
}
