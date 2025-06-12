package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.domain.neo4j.NCoffeeShop;
import com.example.socialcoffee.model.CoffeeShopRecommendationDTO;
import com.example.socialcoffee.model.MoodCount;
import com.example.socialcoffee.model.MoodScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface NCoffeeShopRepository extends Neo4jRepository<NCoffeeShop, Long> {
    @Query("""
            MATCH (target:CoffeeShop {id: $coffeeShopId})-[:HAS_FEATURE]->(f)
            WITH target, collect(id(f)) AS targetFeatureIds

            MATCH (other:CoffeeShop)-[:HAS_FEATURE]->(f2)
            WHERE other.id <> target.id
            WITH other, collect(id(f2)) AS otherFeatureIds, targetFeatureIds

            WITH other,
                 apoc.coll.intersection(targetFeatureIds, otherFeatureIds) AS commonFeatures,
                 targetFeatureIds, otherFeatureIds
            WITH other,
                 size(commonFeatures) AS similarityScore,
                 size(targetFeatureIds) AS targetTotal,
                 size(otherFeatureIds) AS otherTotal,
                 targetFeatureIds,
                 otherFeatureIds

            WITH other,
                 similarityScore,
                 toFloat(similarityScore) / size(apoc.coll.union(targetFeatureIds, otherFeatureIds)) AS jaccardScore

            RETURN other
            ORDER BY jaccardScore DESC
            LIMIT 10
            """)
    List<NCoffeeShop> findRelatedCoffeeShops(@Param("coffeeShopId") Long coffeeShopId);

    @Query("""
            MATCH (target:User {id: $userId})-[:PREFER]->(f)
            WITH target, collect(id(f)) AS targetFeatureIds

            MATCH (other:CoffeeShop)-[:HAS_FEATURE]->(f2)
            WITH other, collect(id(f2)) AS otherFeatureIds, targetFeatureIds

            WITH other,
                 apoc.coll.intersection(targetFeatureIds, otherFeatureIds) AS commonFeatures,
                 targetFeatureIds, otherFeatureIds
            WITH other,
                 size(commonFeatures) AS similarityScore,
                 size(targetFeatureIds) AS targetTotal,
                 size(otherFeatureIds) AS otherTotal,
                 targetFeatureIds,
                 otherFeatureIds

            WITH other,
                 similarityScore,
                 toFloat(similarityScore) / size(apoc.coll.union(targetFeatureIds, otherFeatureIds)) AS jaccardScore

            RETURN other
            ORDER BY jaccardScore DESC
            LIMIT 10
            """)
    List<NCoffeeShop> findRecommendedForYou(@Param("userId") Long userId);

    // 4. Content-Based Filtering with REVIEW (including rating)
    @Query(value = """
                  MATCH (u:User {id: $userId})-[:PREFER]->(f)
                  MATCH (cs:CoffeeShop)-[:HAS_FEATURE]->(f)
                  WHERE NOT (u)-[:LIKE]->(cs)

                  WITH u, cs, collect(f.id) AS matchedFeatures

                  MATCH (u)-[:PREFER]->(allF)
                  WITH u, cs, matchedFeatures, count(allF) AS totalFeatures

                  MATCH (cs)<-[r:REVIEW]-(reviewer:User)
                  WITH cs, matchedFeatures, totalFeatures,
                       count(r) AS totalReviewCount,
                       avg(r.rating) AS avgRating,
                       sum(CASE WHEN duration.inDays(r.createdAt, datetime()).weeks <= $weeks
                                THEN 1
                                ELSE 0
                           END) AS recencyScore

                  RETURN cs.id AS shopId,
                         (
                           0.8 * toFloat(size(matchedFeatures)) / (totalFeatures + 0.0001) +
                           0.1 * (avgRating / 5.0) +
                           0.1 * (toFloat((recencyScore+1)) / (totalReviewCount + 1))
                           ) AS score
                  ORDER BY score DESC
                  LIMIT 10
            """)
    List<CoffeeShopRecommendationDTO> findBasedOnYourPreferences(@Param("userId") Long userId, @Param("weeks") Integer weeks);

    // 2. Collaborative Filtering: Item-Based with REVIEW (including rating)
    @Query(value = """
            MATCH (u:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop)
            WITH u, collect(cs1) AS userLikedShops

            MATCH (cs1)<-[:LIKE]-(u2:User)-[:LIKE]->(cs2:CoffeeShop)
            WHERE NOT (u)-[:LIKE]->(cs2) AND cs2 <> cs1

            WITH u, cs2, userLikedShops, collect(cs1) AS commonShops

            WITH cs2, userLikedShops, commonShops,
                 [x IN commonShops WHERE x IN userLikedShops | x] AS overlap

            WITH cs2,
                 size(overlap) AS shopIntersection,
                 size(userLikedShops) + size(commonShops) - size(overlap) AS shopUnion
            WHERE shopUnion > 0

            MATCH (cs2)<-[r:REVIEW]-(u:User)
            WITH cs2, shopIntersection, shopUnion,
                 count(r) AS reviewCount,
                 avg(r.rating) AS avgRating

            WITH cs2, shopIntersection, shopUnion, reviewCount, avgRating,
                       sum(CASE WHEN duration.inDays(r.createdAt, datetime()).weeks <= $weeks
                                THEN 1
                                ELSE 0
                           END) AS recencyScore

            RETURN cs2.id AS shopId,
                   (
                     0.8 * toFloat(shopIntersection) / shopUnion +
                     0.1 * (avgRating / 5.0) +
                     0.1 * (toFloat((recencyScore+1)) / (reviewCount + 1))
                     ) AS score
            ORDER BY score DESC
            LIMIT 10
            """)
    List<CoffeeShopRecommendationDTO> findSimilarToPlacesYouLike(@Param("userId") Long userId,
                                                                 @Param("weeks") Integer weeks);

    @Query("MATCH (u1:CoffeeShop {id: $coffeeShopId})-[p:HAS_FEATURE]->(f) DELETE p")
    void clearAllFeatures(@Param("coffeeShopId") Long coffeeShopId);

    @Query(value = """
            MATCH (cs:CoffeeShop)<-[m:TAG_MOOD {name: $mood}]-(:User)
            WITH cs, count(m) AS moodCount
            MATCH (cs:CoffeeShop)<-[m2:TAG_MOOD]-(:User)
            WITH cs, moodCount, count(m2) AS totalMoodCount
            WHERE moodCount > $moodCountThreshold

            MATCH (cs)<-[r:REVIEW]-(:User)
            WITH cs, moodCount, totalMoodCount,
                 avg(r.rating) AS avgRating,
                 count(r) AS reviewCount,
                 sum(CASE WHEN duration.inDays(r.createdAt, datetime()).weeks <= $weeks
                          THEN 1
                          ELSE 0
                     END) AS recencyScore
            WHERE avgRating > $avgRatingThreshold

            OPTIONAL MATCH (u:User {id: $userId})-[:LIKE]->(cs)
            WHERE u IS NULL

            RETURN cs.id AS shopId,
                   (
                     0.8 * (moodCount * 1.0 / totalMoodCount) +
                     0.1 * (avgRating / 5.0) +
                     0.1 * (toFloat((recencyScore+1)) / (reviewCount + 1))
                     ) AS score
            ORDER BY score DESC
            SKIP $skip LIMIT $limit
            """,
            countQuery = """
            MATCH (cs:CoffeeShop)<-[m:TAG_MOOD {name: $mood}]-(:User)
            WITH cs, count(m) AS mood_count
            WHERE mood_count > $moodCountThreshold

            MATCH (cs)<-[r:REVIEW]-(:User)
            WITH cs, mood_count,
                 avg(r.rating) AS avg_rating
            WHERE avg_rating > $avgRatingThreshold

            OPTIONAL MATCH (u:User {id: $userId})-[:LIKE]->(cs)
            WHERE u IS NULL

            RETURN count(cs) AS count
            """)
    Page<MoodScore> findTopCoffeeShopByMood(@Param("mood") String mood,
                                            @Param("userId") Long userId,
                                            @Param("moodCountThreshold") Integer moodCountThreshold,
                                            @Param("avgRatingThreshold") Double avgRatingThreshold,
                                            @Param("weeks") Integer weeks,
                                            Pageable pageRequest);

    @Query(value = "MATCH (:User)-[r:TAG_MOOD]->(:CoffeeShop {id: $shopId}) " +
            "RETURN r.name AS mood, COUNT(r) AS count")
    List<MoodCount> getMoodsForCoffeeShop(@Param(value = "shopId") Long shopId);
}
