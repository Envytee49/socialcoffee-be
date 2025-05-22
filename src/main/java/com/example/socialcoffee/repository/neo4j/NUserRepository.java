package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.model.CoffeeShopRecommendationDTO;
import com.example.socialcoffee.neo4j.NUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NUserRepository extends Neo4jRepository<NUser, Long> {
    @Query("MATCH (u1:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop) " +
            "WITH u1, collect(cs1) AS shops1 " +
            "MATCH (u2:User)-[:LIKE]->(cs2:CoffeeShop) " +
            "WHERE u1 <> u2 " +
            "WITH u1, u2, shops1, collect(cs2) AS shops2 " +
            "WITH u1, u2, shops1, shops2, " +
            "     size([x IN shops1 WHERE x IN shops2 | x]) AS likeIntersection, " +
            "     size(shops1) + size(shops2) - size([x IN shops1 WHERE x IN shops2 | x]) AS likeUnion " +
            "MATCH (u1)-[:PREFER]->(f1) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, collect(id(f1)) AS features1 " +
            "MATCH (u2)-[:PREFER]->(f2) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, collect(id(f2)) AS features2 " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, features2, " +
            "     size([x IN features1 WHERE x IN features2 | x]) AS preferIntersection, " +
            "     size(features1) + size(features2) - size([x IN features1 WHERE x IN features2 | x]) AS preferUnion " +
            "WHERE likeUnion > 0 OR preferUnion > 0 " +
            "WITH u1, u2, " +
            "     CASE WHEN likeUnion > 0 THEN toFloat(likeIntersection) / likeUnion ELSE 0.0 END AS jaccardLike, " +
            "     CASE WHEN preferUnion > 0 THEN toFloat(preferIntersection) / preferUnion ELSE 0.0 END AS jaccardPrefer " +
            "RETURN u2 " +
            "ORDER BY (0.3 * jaccardLike + 0.7 * jaccardPrefer) DESC")
    List<NUser> findSimilarUsersByLikesAndPreferences(@Param("userId") Long userId);

    // 1. Collaborative Filtering: User-Based with FOLLOW and REVIEW (including rating)
    @Query("MATCH (u1:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop) " +
            "WITH u1, collect(cs1) AS shops1 " +
            "MATCH (u2:User)-[:LIKE]->(cs2:CoffeeShop) " +
            "WHERE u1 <> u2 " +
            "WITH u1, u2, shops1, collect(cs2) AS shops2 " +
            "WITH u1, u2, shops1, shops2, " +
            "     size([x IN shops1 WHERE x IN shops2 | x]) AS likeIntersection, " +
            "     size(shops1) + size(shops2) - size([x IN shops1 WHERE x IN shops2 | x]) AS likeUnion " +
            "MATCH (u1)-[:PREFER]->(f1) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, collect(id(f1)) AS features1 " +
            "MATCH (u2)-[:PREFER]->(f2) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, collect(id(f2)) AS features2 " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, features2, " +
            "     size([x IN features1 WHERE x IN features2 | x]) AS preferIntersection, " +
            "     size(features1) + size(features2) - size([x IN features1 WHERE x IN features2 | x]) AS preferUnion " +
            "OPTIONAL MATCH (u1)-[:FOLLOW]->(u2) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, features2, preferIntersection, preferUnion, " +
            "     CASE WHEN (u1)-[:FOLLOW]->(u2) THEN 1.0 ELSE 0.0 END AS followScore " +
            "WHERE likeUnion > 0 OR preferUnion > 0 OR followScore > 0 " +
            "WITH u2, (0.3 * toFloat(likeIntersection) / (likeUnion + 0.0001) + " +
            "          0.4 * toFloat(preferIntersection) / (preferUnion + 0.0001) + " +
            "          0.2 * followScore) AS similarityScore " +
            "MATCH (u2)-[:LIKE]->(cs:CoffeeShop) " +
            "WHERE NOT (u1)-[:LIKE]->(cs) AND cs.status = 'ACTIVE' " +
            "WITH cs, max(similarityScore) AS userScore " +
            "MATCH (cs)<-[:REVIEW]-(r:Review) " +
            "WITH cs, userScore, count(r) AS reviewCount, max(r.createdAt) AS latestReview, avg(r.rating) AS avgRating " +
            "WITH cs, userScore, reviewCount, latestReview, avgRating, " +
            "     CASE WHEN latestReview IS NOT NULL " +
            "          THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days)) " +
            "          ELSE 0.0 END AS recencyScore " +
            "RETURN cs.id AS shopId, cs.name AS name, cs.coverPhoto AS coverPhoto, cs.status AS status, " +
            "       (0.6 * userScore + 0.15 * (avgRating / 5.0) + 0.15 * log(reviewCount + 1) + 0.1 * recencyScore) AS score, " +
            "       'Liked by similar or followed users, ranked by ratings and reviews' AS matchReason " +
            "ORDER BY score DESC")
    Page<CoffeeShopRecommendationDTO> findUserBasedRecommendations(@Param("userId") Long userId, Pageable pageable);

    // 2. Collaborative Filtering: Item-Based with REVIEW (including rating)
    @Query("MATCH (u:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop) " +
            "WITH cs1, collect(cs1) AS userLikedShops " +
            "MATCH (cs1)-[:LIKE]-(u2:User)-[:LIKE]->(cs2:CoffeeShop) " +
            "WHERE cs2 <> cs1 AND NOT (u)-[:LIKE]->(cs2) AND cs2.status = 'ACTIVE' " +
            "WITH cs2, userLikedShops, collect(cs1) AS commonShops " +
            "WITH cs2, size([x IN commonShops WHERE x IN userLikedShops | x]) AS shopIntersection, " +
            "     size(userLikedShops) + size(commonShops) - size([x IN commonShops WHERE x IN userLikedShops | x]) AS shopUnion " +
            "WHERE shopUnion > 0 " +
            "MATCH (cs2)<-[:REVIEW]-(r:Review) " +
            "WITH cs2, shopIntersection, shopUnion, count(r) AS reviewCount, max(r.createdAt) AS latestReview, avg(r.rating) AS avgRating " +
            "WITH cs2, shopIntersection, shopUnion, reviewCount, latestReview, avgRating, " +
            "     CASE WHEN latestReview IS NOT NULL " +
            "          THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days)) " +
            "          ELSE 0.0 END AS recencyScore " +
            "RETURN cs2.id AS shopId, cs2.name AS name, cs2.coverPhoto AS coverPhoto, cs2.status AS status, " +
            "       (0.6 * toFloat(shopIntersection) / shopUnion + 0.15 * (avgRating / 5.0) + 0.15 * log(reviewCount + 1) + 0.1 * recencyScore) AS score, " +
            "       'Similar to liked coffee shops, ranked by ratings and reviews' AS matchReason " +
            "ORDER BY score DESC")
    Page<CoffeeShopRecommendationDTO> findItemBasedRecommendations(@Param("userId") Long userId, Pageable pageable);

    // 3. Collaborative Filtering: Follow-Based with REVIEW (including rating)
    @Query("MATCH (u:User {id: $userId})-[:FOLLOW]->(u2:User)-[:LIKE]->(cs:CoffeeShop) " +
            "WHERE NOT (u)-[:LIKE]->(cs) AND cs.status = 'ACTIVE' " +
            "WITH cs, count(*) AS followLikes " +
            "MATCH (cs)<-[:REVIEW]-(r:Review) " +
            "WITH cs, followLikes, count(r) AS reviewCount, max(r.createdAt) AS latestReview, avg(r.rating) AS avgRating " +
            "WITH cs, followLikes, reviewCount, latestReview, avgRating, " +
            "     CASE WHEN latestReview IS NOT NULL " +
            "          THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days)) " +
            "          ELSE 0.0 END AS recencyScore " +
            "RETURN cs.id AS shopId, cs.name AS name, cs.coverPhoto AS coverPhoto, cs.status AS status, " +
            "       (0.6 * toFloat(followLikes) / (followLikes + 10) + 0.15 * (avgRating / 5.0) + 0.15 * log(reviewCount + 1) + 0.1 * recencyScore) AS score, " +
            "       'Liked by followed users, ranked by ratings and reviews' AS matchReason " +
            "ORDER BY score DESC")
    Page<CoffeeShopRecommendationDTO> findFollowBasedRecommendations(@Param("userId") Long userId, Pageable pageable);

    // 4. Content-Based Filtering with REVIEW (including rating)
    @Query("MATCH (u:User {id: $userId})-[:PREFER]->(f:NFeature) " +
            "MATCH (cs:CoffeeShop)-[:HAS_FEATURE]->(f) " +
            "WHERE NOT (u)-[:LIKE]->(cs) AND cs.status = 'ACTIVE' " +
            "WITH cs, collect(f.id) AS matchedFeatures " +
            "MATCH (cs)<-[:REVIEW]-(r:Review) " +
            "WITH cs, matchedFeatures, count(r) AS reviewCount, max(r.createdAt) AS latestReview, avg(r.rating) AS avgRating " +
            "WITH cs, matchedFeatures, reviewCount, latestReview, avgRating, " +
            "     CASE WHEN latestReview IS NOT NULL " +
            "          THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days)) " +
            "          ELSE 0.0 END AS recencyScore " +
            "RETURN cs.id AS shopId, cs.name AS name, cs.coverPhoto AS coverPhoto, cs.status AS status, " +
            "       (0.6 * toFloat(size(matchedFeatures)) / (size((cs)-[:HAS_FEATURE]->()) + 0.0001) + " +
            "        0.15 * (avgRating / 5.0) + 0.15 * log(reviewCount + 1) + 0.1 * recencyScore) AS score, " +
            "       'Matches preferred features, ranked by ratings and reviews' AS matchReason " +
            "ORDER BY score DESC")
    Page<CoffeeShopRecommendationDTO> findContentBasedRecommendations(@Param("userId") Long userId, Pageable pageable);

    // 5. Hybrid: Combine User-Based, Follow-Based, Content-Based, and REVIEW (including rating)
    @Query("MATCH (u1:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop) " +
            "WITH u1, collect(cs1) AS shops1 " +
            "MATCH (u2:User)-[:LIKE]->(cs2:CoffeeShop) " +
            "WHERE u1 <> u2 " +
            "WITH u1, u2, shops1, collect(cs2) AS shops2 " +
            "WITH u1, u2, shops1, shops2, " +
            "     size([x IN shops1 WHERE x IN shops2 | x]) AS likeIntersection, " +
            "     size(shops1) + size(shops2) - size([x IN shops1 WHERE x IN shops2 | x]) AS likeUnion " +
            "MATCH (u1)-[:PREFER]->(f1) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, collect(id(f1)) AS features1 " +
            "MATCH (u2)-[:PREFER]->(f2) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, collect(id(f2)) AS features2 " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, features2, " +
            "     size([x IN features1 WHERE x IN features2 | x]) AS preferIntersection, " +
            "     size(features1) + size(features2) - size([x IN features1 WHERE x IN features2 | x]) AS preferUnion " +
            "OPTIONAL MATCH (u1)-[:FOLLOW]->(u2) " +
            "WITH u1, u2, shops1, shops2, likeIntersection, likeUnion, features1, features2, preferIntersection, preferUnion, " +
            "     CASE WHEN (u1)-[:FOLLOW]->(u2) THEN 1.0 ELSE 0.0 END AS followScore " +
            "WHERE likeUnion > 0 OR preferUnion > 0 OR followScore > 0 " +
            "WITH u2, (0.3 * toFloat(likeIntersection) / (likeUnion + 0.0001) + " +
            "          0.4 * toFloat(preferIntersection) / (preferUnion + 0.0001) + " +
            "          0.3 * followScore) AS userSimilarityScore " +
            "MATCH (u2)-[:LIKE]->(cs:CoffeeShop) " +
            "WHERE NOT (u1)-[:LIKE]->(cs) AND cs.status = 'ACTIVE' " +
            "WITH cs, max(userSimilarityScore) AS userScore " +
            "MATCH (u1)-[:PREFER]->(f:NFeature) " +
            "MATCH (cs)-[:HAS_FEATURE]->(f) " +
            "WITH cs, userScore, collect(f.id) AS matchedFeatures " +
            "MATCH (cs)<-[:REVIEW]-(r:Review) " +
            "WITH cs, userScore, matchedFeatures, count(r) AS reviewCount, max(r.createdAt) AS latestReview, avg(r.rating) AS avgRating " +
            "WITH cs, userScore, matchedFeatures, reviewCount, latestReview, avgRating, " +
            "     CASE WHEN latestReview IS NOT NULL " +
            "          THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days)) " +
            "          ELSE 0.0 END AS recencyScore " +
            "RETURN cs.id AS shopId, cs.name AS name, cs.coverPhoto AS coverPhoto, cs.status AS status, " +
            "       (0.5 * userScore + 0.3 * toFloat(size(matchedFeatures)) / (size((cs)-[:HAS_FEATURE]->()) + 0.0001) + " +
            "        0.1 * (avgRating / 5.0) + 0.05 * log(reviewCount + 1) + 0.05 * recencyScore) AS score, " +
            "       'Based on similar users, followed users, preferred features, ratings, and reviews' AS matchReason " +
            "ORDER BY score DESC")
    Page<CoffeeShopRecommendationDTO> findHybridRecommendations(@Param("userId") Long userId, Pageable pageable);

    @Query("MATCH (u1:User {id: $userId})-[p:PREFER]->(f) DELETE p")
    void clearAllPreferences(@Param("userId") Long userId);
}
