package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.VisitTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitTimeRepository extends JpaRepository<VisitTime, Long> {
}
