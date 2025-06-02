package com.example.socialcoffee.domain.neo4j.feature;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Purpose")
@Setter
@Getter
public class NPurpose extends BaseFeature {
}
