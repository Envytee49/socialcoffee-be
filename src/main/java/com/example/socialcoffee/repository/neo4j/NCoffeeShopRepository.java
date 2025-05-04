package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.NCoffeeShop;
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
}
