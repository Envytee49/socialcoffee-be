package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.BaseFeature;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NFeatureRepository extends Neo4jRepository<BaseFeature, Long> {
}
