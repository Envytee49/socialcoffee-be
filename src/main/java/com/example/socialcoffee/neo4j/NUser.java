package com.example.socialcoffee.neo4j;

import com.example.socialcoffee.neo4j.relationship.Follow;
import com.example.socialcoffee.neo4j.relationship.Prefer;
import com.example.socialcoffee.neo4j.relationship.Like;
import com.example.socialcoffee.neo4j.relationship.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
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
    public void clearAllPreferences() {
        if (!CollectionUtils.isEmpty(this.preferCoffeeShops)) {
            this.preferCoffeeShops.clear();
        }
    }
    public void addFollowing(NUser u2) {
        if (CollectionUtils.isEmpty(this.followUsers)) {
            this.followUsers = new HashSet<>();
        }
        this.followUsers.add(Follow.builder()
                                     .user(u2)
                                     .build());
    }

    public void removeFollowing(NUser u2) {
        if (CollectionUtils.isEmpty(this.followUsers)) {
            return;
        }
        this.followUsers.remove(Follow.builder()
                                     .user(u2)
                                     .build());
    }

    public void addLike(NCoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.likeCoffeeShops)) {
            this.likeCoffeeShops = new HashSet<>();
        }
        this.likeCoffeeShops.add(Like.builder()
                                     .coffeeShop(coffeeShop)
                                     .build());
    }

    public void removeLike(NCoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.likeCoffeeShops)) {
            return;
        }
        this.likeCoffeeShops.remove(Like.builder()
                                        .coffeeShop(coffeeShop)
                                        .build());
    }
}
