package com.example.socialcoffee.neo4j.relationship;

import com.example.socialcoffee.neo4j.NCoffeeShop;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipProperties
@Getter
@Setter
@Builder
public class Review {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Id
    private Long id;

    private Integer rating;

    @TargetNode
    private NCoffeeShop coffeeShop;

    @Override
    public int hashCode() {
        return Objects.hashCode(coffeeShop);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Review review)) return false;
        return Objects.equals(coffeeShop.getId(), review.coffeeShop.getId());
    }
}
