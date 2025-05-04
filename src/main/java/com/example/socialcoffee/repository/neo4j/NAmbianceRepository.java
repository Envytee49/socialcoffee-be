package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NAmbiance;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NAmbianceRepository extends Neo4jRepository<NAmbiance, Long> {
}
