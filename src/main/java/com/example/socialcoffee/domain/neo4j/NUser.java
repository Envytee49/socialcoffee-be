package com.example.socialcoffee.domain.neo4j;

import com.example.socialcoffee.domain.neo4j.relationship.*;
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

    @Relationship(type = "TAG_MOOD", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private Set<TagMood> tagMoodCoffeeShops;

    public void addFollowing(Neo4jClient neo4jClient, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("myId", this.id);
        params.put("userId", userId);
        params.put("createdAt", LocalDateTime.now());
        params.put("updatedAt", LocalDateTime.now());

        neo4jClient.query(
                "MATCH (u1:User {id: $myId}), (u2:User {id: $userId}) " +
                        "MERGE (u1)-[r:FOLLOW {createdAt: $createdAt, updatedAt: $updatedAt}]->(u2)"
        ).bindAll(params).run();
    }

    public void removeFollowing(Neo4jClient neo4jClient, Long userId) {
        if (CollectionUtils.isEmpty(this.followUsers)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", this.id);
        params.put("userId", userId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:FOLLOW]-> (m:User {id: $userId}) DELETE f")
                .bindAll(params).run();
    }

    public void addLike(Neo4jClient neo4jClient, Long coffeeShopId) {
        Map<String, Object> params = new HashMap<>();
        params.put("myId", this.id);
        params.put("coffeeShopId", coffeeShopId);
        params.put("createdAt", LocalDateTime.now());
        params.put("updatedAt", LocalDateTime.now());

        neo4jClient.query(
                "MATCH (u:User {id: $myId}), (cs:CoffeeShop {id: $coffeeShopId}) " +
                        "MERGE (u)-[r:LIKE {createdAt: $createdAt, updatedAt: $updatedAt}]->(cs)"
        ).bindAll(params).run();
    }

    public void removeLike(Neo4jClient neo4jClient, Long coffeeShopId) {
        if (CollectionUtils.isEmpty(this.likeCoffeeShops)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", this.id);
        params.put("coffeeShopId", coffeeShopId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:LIKE]-> (m:CoffeeShop {id: $coffeeShopId}) DELETE f")
                .bindAll(params).run();
    }

    public void addReview(Neo4jClient neo4jClient, Long coffeeShopId, Long reviewId, Integer rating) {
        if (CollectionUtils.isEmpty(this.reviewCoffeeShops)) {
            this.reviewCoffeeShops = new HashSet<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", this.id);
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

    public void removeReview(Neo4jClient neo4jClient, Long coffeeShopId, Long reviewId) {
        if (CollectionUtils.isEmpty(this.reviewCoffeeShops)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("myId", this.id);
        params.put("coffeeShopId", coffeeShopId);
        params.put("reviewId", reviewId);
        neo4jClient.query("MATCH (u:User {id: $myId}) -[f:REVIEW {id: $reviewId}]-> (m:CoffeeShop {id: $coffeeShopId}) DELETE f")
                .bindAll(params).run();
    }

    public void addTagMood(Neo4jClient neo4jClient, Long coffeeShopId, String moodName) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", this.id);
        params.put("coffeeShopId", coffeeShopId);
        params.put("name", moodName);
        params.put("createdAt", LocalDateTime.now());
        params.put("updatedAt", LocalDateTime.now());

        neo4jClient.query(
                "MATCH (u:User {id: $userId}), (cs:CoffeeShop {id: $coffeeShopId}) " +
                        "MERGE (u)-[r:TAG_MOOD {name: $name, createdAt: $createdAt, updatedAt: $updatedAt}]->(cs)"
        ).bindAll(params).run();
    }

    public void removeTagMood(Neo4jClient neo4jClient, Long coffeeShopId, String moodName) {
        if (CollectionUtils.isEmpty(this.tagMoodCoffeeShops)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", this.id);
        params.put("coffeeShopId", coffeeShopId);
        params.put("name", moodName);
        neo4jClient.query("MATCH (u:User {id: $userId}) -[f:TAG_MOOD {name: $name}]-> (m:CoffeeShop {id: $coffeeShopId}) DELETE f")
                .bindAll(params).run();
    }
}