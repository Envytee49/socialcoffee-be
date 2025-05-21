package com.example.socialcoffee.neo4j.feature;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseFeature implements NFeature {
    @Id
    protected Long id;
    protected String name;
}
