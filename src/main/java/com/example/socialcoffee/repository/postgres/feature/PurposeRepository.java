package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurposeRepository extends JpaRepository<Purpose, Long> {
}
