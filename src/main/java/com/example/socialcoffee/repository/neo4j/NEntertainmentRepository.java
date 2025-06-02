package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.domain.neo4j.feature.NEntertainment;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NEntertainmentRepository extends Neo4jRepository<NEntertainment, Long> {
}
