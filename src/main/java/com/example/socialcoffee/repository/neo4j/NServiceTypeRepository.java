package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NServiceType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NServiceTypeRepository extends Neo4jRepository<NServiceType, Long> {
}
