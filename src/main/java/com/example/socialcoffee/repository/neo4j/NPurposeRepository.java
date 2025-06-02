package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.domain.neo4j.feature.NPurpose;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NPurposeRepository extends Neo4jRepository<NPurpose, Long> {
}
