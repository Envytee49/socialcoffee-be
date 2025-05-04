package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NVisitTime;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NVisitTimeRepository extends Neo4jRepository<NVisitTime, Long> {
}
