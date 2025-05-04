package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NAmenity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NAmenityRepository extends Neo4jRepository<NAmenity, Long> {
}
