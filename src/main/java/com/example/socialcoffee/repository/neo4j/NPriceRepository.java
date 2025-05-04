package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.feature.NPrice;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NPriceRepository extends Neo4jRepository<NPrice, Long> {
}
