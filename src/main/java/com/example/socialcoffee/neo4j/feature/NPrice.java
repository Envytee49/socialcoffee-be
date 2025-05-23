package com.example.socialcoffee.neo4j.feature;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Price")
@Setter
@Getter
public class NPrice extends BaseFeature {
}
