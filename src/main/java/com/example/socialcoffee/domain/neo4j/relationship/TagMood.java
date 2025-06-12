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
public class TagMood {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Id
    @GeneratedValue
    private String id;

    private String name;

    @TargetNode
    private NCoffeeShop coffeeShop;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TagMood tagMood = (TagMood) o;
        return Objects.equals(id, tagMood.id) && Objects.equals(coffeeShop.getId(), tagMood.coffeeShop.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, coffeeShop.getId());
    }
}
