package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NSpace;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NSpaceRepository extends Neo4jRepository<NSpace, Long> {
}
