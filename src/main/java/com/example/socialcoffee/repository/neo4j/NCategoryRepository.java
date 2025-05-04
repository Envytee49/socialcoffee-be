package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NCategory;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NCategoryRepository extends Neo4jRepository<NCategory, Long> {
}
