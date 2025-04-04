package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.CoffeeShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, Long>, JpaSpecificationExecutor<CoffeeShop> {
    @Query(value = "SELECT id, description FROM coffee_shops ORDER BY description_embedding <=> (:embedding)::VECTOR(384) LIMIT :limit", nativeQuery = true)
    List<Object[]> findSimilarCoffeeShops(@Param("embedding") String embedding, @Param("limit") int limit);

    @Query(value = "SELECT c FROM CoffeeShop c WHERE c.id = :shopId")
    CoffeeShop findByShopId(@Param("shop_id") Long shopId);
}
