package com.example.socialcoffee.repository.neo4j;

import com.example.socialcoffee.neo4j.NUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NUserRepository extends Neo4jRepository<NUser, Long> {
}
