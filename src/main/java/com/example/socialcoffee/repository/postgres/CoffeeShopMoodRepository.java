package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.CoffeeShopMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface CoffeeShopMoodRepository extends JpaRepository<CoffeeShopMood, Long> {
    @Query(value = "SELECT csm FROM CoffeeShopMood WHERE csm.shop_id = :shopId AND csm.user_id = :userId AND csm.mood = :mood")
    CoffeeShopMood findByShopIdAndUserIdAndMood(Long shopId, Long userId, String mood);

    List<CoffeeShopMood> findByShopIdAndUserId(Long shopId, Long userId);

    void deleteByShopIdAndUserId(Long shopId, Long userId);

    List<CoffeeShopMood> findByShopId(Long shopId);
}
