package com.example.socialcoffee.domain.neo4j.relationship;

import com.example.socialcoffee.domain.neo4j.NCoffeeShop;
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
public class Like {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private NCoffeeShop coffeeShop;

    @Override
    public int hashCode() {
        return Objects.hashCode(coffeeShop);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Like like)) return false;
        return Objects.equals(coffeeShop.getId(),
                like.coffeeShop.getId());
    }
}

