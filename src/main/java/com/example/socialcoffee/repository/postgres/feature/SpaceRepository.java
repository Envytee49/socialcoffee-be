package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
}
