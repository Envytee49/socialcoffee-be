package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.domain.neo4j.feature.NParking;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NParkingRepository extends Neo4jRepository<NParking, Long> {
}
