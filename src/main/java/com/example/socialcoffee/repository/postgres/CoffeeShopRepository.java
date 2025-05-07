package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.repository.postgres.custom.CoffeeShopRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, Long>, JpaSpecificationExecutor<CoffeeShop>, CoffeeShopRepositoryCustom {
    @Query(value = "SELECT cs.id FROM coffee_shops cs " +
            "JOIN description_embedding de ON cs.embedding_description_id = de.id " +
            "ORDER BY de.description_embedding <=> CAST(:embedding AS VECTOR(384)) " +
            "LIMIT :limit", nativeQuery = true)
    List<Long> findSimilarCoffeeShops(@Param("embedding") String embedding, @Param("limit") int limit);

    @Query(value = "SELECT c FROM CoffeeShop c WHERE c.id = :shopId")
    CoffeeShop findByShopId(@Param("shopId") Long shopId);

    Page<CoffeeShop> findByNameContainingIgnoreCaseAndStatus(String name, String status, Pageable pageable);

    Page<CoffeeShop> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<CoffeeShop> findByStatus(String status, Pageable pageable);

    @Query("SELECT cs.createdBy, COUNT(cs) FROM CoffeeShop cs GROUP BY cs.createdBy ORDER BY COUNT(cs) DESC LIMIT :limit")
    List<Object[]> findTopContributors(@Param("limit") int limit);

    @Query("SELECT cs " +
            "FROM CoffeeShop cs " +
            "LEFT JOIN cs.reviews r " +
            "LEFT JOIN Collection c ON cs MEMBER OF c.coffeeShops " +
            "GROUP BY cs " +
            "ORDER BY (" +
            ":a * SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) + " +
            ":b * SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) + " +
            ":c * SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) + " +
            ":d * SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) + " +
            ":e * SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END)) DESC, " +
            "COUNT(DISTINCT c) DESC")
    List<CoffeeShop> findTop10CoffeeShopsByWeightedRatingAndCollections(
            @Param("a") double a,
            @Param("b") double b,
            @Param("c") double c,
            @Param("d") double d,
            @Param("e") double e);

    @Query("SELECT cs " +
            "FROM CoffeeShop cs " +
            "LEFT JOIN cs.reviews r " +
            "LEFT JOIN Collection c ON cs MEMBER OF c.coffeeShops " +
            "WHERE r.createdAt BETWEEN :startDate AND :endDate OR r IS NULL " +
            "GROUP BY cs " +
            "ORDER BY (" +
            ":a * SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) + " +
            ":b * SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) + " +
            ":c * SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) + " +
            ":d * SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) + " +
            ":e * SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END)) DESC, " +
            "COUNT(DISTINCT c) DESC")
    List<CoffeeShop> findTrendingCoffeeShops(
            @Param("a") double a,
            @Param("b") double b,
            @Param("c") double c,
            @Param("d") double d,
            @Param("e") double e,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
