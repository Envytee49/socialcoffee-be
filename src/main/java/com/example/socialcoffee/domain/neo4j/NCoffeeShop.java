package com.example.socialcoffee.domain.neo4j;

import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.domain.neo4j.relationship.HasFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("CoffeeShop")
@Getter
@Setter
@Builder
public class NCoffeeShop {
    @Id
    private Long id;

    private String name;

    private String coverPhoto;

    private String status = Status.ACTIVE.getValue();

    @JsonIgnore
    @Relationship(type = "HAS_FEATURE", direction = Relationship.Direction.OUTGOING)
    private Set<HasFeature> hasFeatures;
}
