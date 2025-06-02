package com.example.socialcoffee.domain.neo4j.feature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
