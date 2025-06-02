package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.Entertainment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntertainmentRepository extends JpaRepository<Entertainment, Long> {
}
