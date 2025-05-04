package com.example.socialcoffee.neo4j.feature;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Amenity")
@Setter
@Getter
public class NAmenity extends BaseFeature {
}
