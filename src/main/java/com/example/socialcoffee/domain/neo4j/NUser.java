package com.example.socialcoffee.domain.neo4j;

import com.example.socialcoffee.domain.neo4j.relationship.Prefer;
import com.example.socialcoffee.domain.neo4j.relationship.Review;
import com.example.socialcoffee.domain.neo4j.relationship.Follow;
import com.example.socialcoffee.domain.neo4j.relationship.Like;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Node("User")
@Setter
@Getter
@Builder
public class NUser {
    @Id
    private Long id;

    private String displayName;

    private String profilePhoto;

    @Relationship(type = "LIKE", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Set<Like> likeCoffeeShops;

    @Relationship(type = "PREFER", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Set<Prefer> preferCoffeeShops;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Set<Follow> followUsers;

    @Relationship(type = "REVIEW", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Set<Review> reviewCoffeeShops;

    public void addFollowing(NUser u2) {
        if (CollectionUtils.isEmpty(this.followUsers)) {
            this.followUsers = new HashSet<>();
        }
        this.followUsers.add(Follow.builder()
                .user(u2)
                .build());
    }

    public void removeFollowing(Neo4jClient neo4jClient, Long myId, Long userId) {
        if (CollectionUtils.isEmpty(this.followUsers)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", myId);
        params.put("userId", userId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:FOLLOW]-> (m:User {id: $userId}) DELETE f")
                .bindAll(params).run();
    }

    public void addLike(NCoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.likeCoffeeShops)) {
            this.likeCoffeeShops = new HashSet<>();
        }
        this.likeCoffeeShops.add(Like.builder()
                .coffeeShop(coffeeShop)
                .build());
    }

    public void removeLike(Neo4jClient neo4jClient, Long myId, Long coffeeShopId) {
        if (CollectionUtils.isEmpty(this.likeCoffeeShops)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", myId);
        params.put("coffeeShopId", coffeeShopId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:LIKE]-> (m:CoffeeShop {id: $coffeeShopId}) DELETE f")
                .bindAll(params).run();
    }

    public void addReview(Long userId, Long coffeeShopId, Long reviewId, Integer rating, Neo4jClient neo4jClient) {
        if (CollectionUtils.isEmpty(this.reviewCoffeeShops)) {
            this.reviewCoffeeShops = new HashSet<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("coffeeShopId", coffeeShopId);
        params.put("rating", rating);
        params.put("reviewId", reviewId);
        params.put("createdAt", LocalDateTime.now());
        params.put("updatedAt", LocalDateTime.now());

        neo4jClient.query(
                "MATCH (u:User {id: $userId}), (cs:CoffeeShop {id: $coffeeShopId}) " +
                        "MERGE (u)-[r:REVIEW {id: $reviewId, rating: $rating, createdAt: $createdAt, updatedAt: $updatedAt}]->(cs)"
        ).bindAll(params).run();
    }

    public void removeReview(Neo4jClient neo4jClient, Long myId, Long coffeeShopId, Long reviewId) {
        if (CollectionUtils.isEmpty(this.reviewCoffeeShops)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", myId);
        params.put("coffeeShopId", coffeeShopId);
        params.put("reviewId", reviewId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:REVIEW {id: $reviewId}]-> (m:CoffeeShop {id: $coffeeShopId}) DELETE f")
                .bindAll(params).run();
    }
}
