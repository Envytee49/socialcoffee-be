package com.example.socialcoffee.domain.neo4j.feature;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;


@Node("Parking")
@Setter
@Getter
public class NParking extends BaseFeature {
}
