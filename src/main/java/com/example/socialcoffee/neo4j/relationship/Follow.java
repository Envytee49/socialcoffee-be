package com.example.socialcoffee.neo4j.relationship;

import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.neo4j.feature.NFeature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipProperties
@Getter
@Setter
@Builder
public class Follow {
    @Id
    @GeneratedValue
    private String id;
    @CreatedDate
    protected LocalDateTime createdAt;
    @LastModifiedDate
    protected LocalDateTime updatedAt;
    @TargetNode
    private NUser user;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Follow follow)) return false;
        return Objects.equals(user.getId(),
                              follow.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }
}
