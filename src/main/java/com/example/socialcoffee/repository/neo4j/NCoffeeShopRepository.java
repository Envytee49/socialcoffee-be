package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.model.CoffeeShopRecommendationDTO;
import com.example.socialcoffee.domain.neo4j.NCoffeeShop;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

                       MATCH (cs)-[:HAS_FEATURE]->(allF)
                       WITH u, cs, matchedFeatures, count(allF) AS totalFeatures

                       MATCH (cs)<-[r:REVIEW]-(reviewer:User)
                       WITH cs, matchedFeatures, totalFeatures,
                            count(r) AS reviewCount,
                            max(r.createdAt) AS latestReview,
                            avg(r.rating) AS avgRating

                       WITH cs, matchedFeatures, totalFeatures, reviewCount, latestReview, avgRating,
                            CASE WHEN latestReview IS NOT NULL
                                 THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days))
                                 ELSE 0.0
                            END AS recencyScore

                       RETURN cs.id AS shopId,
                              (
                                0.6 * toFloat(size(matchedFeatures)) / (totalFeatures + 0.0001) +
                                0.15 * (avgRating / 5.0) +
                                0.15 * log(reviewCount + 1) +
                                0.1 * recencyScore
                              ) AS score
                       ORDER BY score DESC
                       LIMIT 10
               """)
    List<CoffeeShopRecommendationDTO> findBasedOnYourPreferences(@Param("userId") Long userId);

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
                    max(r.createdAt) AS latestReview,
                    avg(r.rating) AS avgRating

               WITH cs2, shopIntersection, shopUnion, reviewCount, latestReview, avgRating,
                    CASE WHEN latestReview IS NOT NULL
                         THEN toFloat(1.0 / (1 + duration.between(latestReview, datetime()).days))
                         ELSE 0.0
                    END AS recencyScore

               RETURN cs2.id AS shopId,
                      (
                        0.6 * toFloat(shopIntersection) / shopUnion +
                        0.15 * (avgRating / 5.0) +
                        0.15 * log(reviewCount + 1) +
                        0.1 * recencyScore
                      ) AS score
               ORDER BY score DESC
               LIMIT 10
               """)
    List<CoffeeShopRecommendationDTO> findSimilarToPlacesYouLike(@Param("userId") Long userId);

    @Query("MATCH (u1:CoffeeShop {id: $coffeeShopId})-[p:HAS_FEATURE]->(f) DELETE p")
    void clearAllFeatures(@Param("coffeeShopId") Long coffeeShopId);
}
