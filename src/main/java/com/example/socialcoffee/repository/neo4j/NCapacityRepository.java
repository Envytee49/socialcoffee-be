package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NCapacity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NCapacityRepository extends Neo4jRepository<NCapacity, Long> {
}
