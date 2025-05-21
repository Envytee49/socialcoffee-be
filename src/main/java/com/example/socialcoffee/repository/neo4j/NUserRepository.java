package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.NUser;
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

    @Query("MATCH (u1:User {id: $userId})-[p:PREFER]->(f) DELETE p")
    void clearAllPreferences(@Param("userId") Long userId);
}
