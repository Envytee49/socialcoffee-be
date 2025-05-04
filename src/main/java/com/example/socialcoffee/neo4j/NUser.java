package com.example.socialcoffee.neo4j;

import com.example.socialcoffee.neo4j.relationship.Follow;
import com.example.socialcoffee.neo4j.relationship.Prefer;
import com.example.socialcoffee.neo4j.relationship.Like;
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
    private Set<Like> likeCoffeeShops;

    @Relationship(type = "PREFER", direction = Relationship.Direction.OUTGOING)
    private Set<Prefer> preferCoffeeShops;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    private Set<Follow> followUsers;

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
}
