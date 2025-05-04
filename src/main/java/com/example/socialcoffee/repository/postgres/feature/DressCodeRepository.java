package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.feature.DressCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DressCodeRepository extends JpaRepository<DressCode, Long> {
}
