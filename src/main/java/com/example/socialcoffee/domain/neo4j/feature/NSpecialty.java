package com.example.socialcoffee.domain.neo4j.feature;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Specialty")
@Setter
@Getter
public class NSpecialty extends BaseFeature {
}
