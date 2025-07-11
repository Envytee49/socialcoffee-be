package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.model.CoffeeShopRecommendationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
            "     size(features1) + size(features2) - size([x IN features1 WHERE x IN features2 | x]) AS preferUnion "
            +
            "WHERE likeUnion > 0 OR preferUnion > 0 " +
            "WITH u1, u2, " +
            "     CASE WHEN likeUnion > 0 THEN toFloat(likeIntersection) / likeUnion ELSE 0.0 END AS jaccardLike, " +
            "     CASE WHEN preferUnion > 0 THEN toFloat(preferIntersection) / preferUnion ELSE 0.0 END AS jaccardPrefer "
            +
            "RETURN u2 " +
            "ORDER BY (0.3 * jaccardLike + 0.7 * jaccardPrefer) DESC")
    List<NUser> findSimilarUsersByLikesAndPreferences(@Param("userId") Long userId);

    // 1. Collaborative Filtering: User-Based with FOLLOW and REVIEW (including
    // rating)
    @Query(value = """
            MATCH (u1:User {id: $userId})-[:LIKE]->(cs1:CoffeeShop)
                                    WITH u1, collect(cs1) AS shops1
                                    MATCH (u2:User)-[:LIKE]->(cs2:CoffeeShop)
                                    WHERE id(u1) <> id(u2)
                                    WITH u1, u2, shops1, collect(cs2) AS shops2
                                    WITH u1, u2, shops1, shops2,
                                         [x IN shops1 WHERE x IN shops2 | x] AS likeIntersectionList
                                    WITH u1, u2, shops1, shops2,
                                         size(likeIntersectionList) AS likeIntersection,
                                         size(shops1) + size(shops2) - size(likeIntersectionList) AS likeUnion

                                    MATCH (u1)-[:PREFER]->(f1)
                                    WITH u1, u2, likeIntersection, likeUnion, collect(id(f1)) AS features1

                                    MATCH (u2)-[:PREFER]->(f2)
                                    WITH u1, u2, likeIntersection, likeUnion, features1, collect(id(f2)) AS features2
                                    WITH u1, u2, likeIntersection, likeUnion, features1, features2,
                                         [x IN features1 WHERE x IN features2 | x] AS preferIntersectionList
                                    WITH u1, u2, likeIntersection, likeUnion, features1, features2,
                                         size(preferIntersectionList) AS preferIntersection,
                                         size(features1) + size(features2) - size(preferIntersectionList) AS preferUnion

                                    OPTIONAL MATCH (u1)-[:FOLLOW]->(u2)
                                    WITH u1, u2, likeIntersection, likeUnion, preferIntersection, preferUnion,
                                         CASE WHEN count(*) > 0 THEN 1.0 ELSE 0.0 END AS followScore
                                    WHERE likeUnion > 0 OR preferUnion > 0 OR followScore > 0

                                    WITH u1, u2,
                                         0.3 * CASE WHEN likeUnion = 0 THEN 0 ELSE toFloat(likeIntersection) / likeUnion END +
                                         0.4 * CASE WHEN preferUnion = 0 THEN 0 ELSE toFloat(preferIntersection) / preferUnion END +
                                         0.2 * followScore AS similarityScore

                                    MATCH (u2)-[:LIKE]->(cs:CoffeeShop)
                                    WHERE NOT (u1)-[:LIKE]->(cs)
                                    WITH cs, max(similarityScore) AS userScore

                                    MATCH (cs)<-[r:REVIEW]-(u:User)
                                    WITH cs, userScore, count(r) AS reviewCount, avg(r.rating) AS avgRating,
                                         sum(CASE WHEN duration.inDays(r.createdAt, datetime()).weeks <= $weeks
                                                                    THEN 1
                                                                    ELSE 0
                                                                    END) AS recencyScore

                                    RETURN cs.id AS shopId,
                                           (0.8 * userScore +
                                            0.1 * (avgRating / 5.0) +
                                            0.1 * (toFloat((recencyScore+1)) / (reviewCount + 1))
                                            ) AS score
                                    ORDER BY score DESC
                                    LIMIT 10
              """)
    List<CoffeeShopRecommendationDTO> findYouMayLikeRecommendation(@Param("userId") Long userId, @Param("weeks") Integer weeks);

    // 3. Collaborative Filtering: Follow-Based with REVIEW (including rating)
    @Query(value = """
            MATCH (u:User {id: $userId})-[:FOLLOW]->(u2:User)-[:LIKE]->(cs:CoffeeShop)
                     WHERE NOT (u)-[:LIKE]->(cs)

                     WITH cs, count(*) AS followLikes

                     MATCH (cs)<-[r:REVIEW]-(reviewer:User)
                     WITH cs, followLikes,
                          count(r) AS reviewCount,
                          avg(r.rating) AS avgRating,
                         sum(CASE WHEN duration.inDays(r.createdAt, datetime()).weeks <= $weeks
                                                    THEN 1
                                                    ELSE 0
                                                    END) AS recencyScore

                     RETURN cs.id AS shopId,
                            (
                              0.8 * toFloat(followLikes) / (followLikes + 10) +
                              0.1 * (avgRating / 5.0) +
                              0.1 * (toFloat((recencyScore+1)) / (reviewCount + 1))
                              ) AS score
                     ORDER BY score DESC
                     LIMIT 10
            """)
    List<CoffeeShopRecommendationDTO> findLikedByPeopleYouFollow(@Param("userId") Long userId, @Param("weeks") Integer weeks);

    @Query(value = "MATCH (u1:User {id: $userId})-[p:PREFER]->(f) DELETE p")
    void clearAllPreferences(@Param("userId") Long userId);

    @Query(value = "MATCH (u:User {id: $userId})-[r:LIKE]->(cs:CoffeeShop {id: $shopId}) " +
            "RETURN count(r) > 0")
    boolean userLikeExist(@Param(value = "userId") Long userId, @Param(value = "shopId") Long shopId);

    @Query(value = "MATCH (u:User {id: $userId})-[r:FOLLOW]->(u2:User {id: $followingWhoId}) " +
            "RETURN count(r) > 0")
    boolean userFollowExist(@Param(value = "userId") Long userId, @Param(value = "followingWhoId") Long followingWhoId);

    @Query(value = "MATCH (u:User {id: $userId})-[r:TAG_MOOD {name: $mood}]->(cs:CoffeeShop {id: $shopId}) " +
            "RETURN count(r) > 0 AS exists")
    boolean tagMoodCoffeeShopExist(@Param(value = "userId") Long userId, @Param(value = "shopId") Long shopId,
                                   @Param(value = "mood") String mood);

    @Query(value = "MATCH (u:User {id: $userId}) <-[r:FOLLOW]- (u2:User) RETURN u2",
            countQuery = "MATCH (u:User {id: $userId}) -[r:FOLLOW]-> (u2:User) RETURN count(u2)")
    Page<NUser> findFollowers(Long userId, Pageable pageable);

    @Query(value = "MATCH (u:User {id: $userId}) -[r:FOLLOW]-> (u2:User) RETURN u2",
            countQuery = "MATCH (u:User {id: $userId}) -[r:FOLLOW]-> (u2:User) RETURN count(u2)")
    Page<NUser> findFollowings(Long userId, Pageable pageable);

    @Query(value = "MATCH (u:User {id: $userId})-[r:TAG_MOOD]->(:CoffeeShop {id: $shopId}) " +
            "RETURN r.name")
    List<String> getUserMoodForCoffeeShop(@Param(value = "userId") Long userId, @Param(value = "shopId") Long shopId);

    @Query(value = "MATCH (u:User {id: $userId}) RETURN u")
    Optional<NUser> findUser(@Param(value = "userId") Long userId);
}
