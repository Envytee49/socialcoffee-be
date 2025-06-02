package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.Ambiance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbianceRepository extends JpaRepository<Ambiance, Long> {
}
