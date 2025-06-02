package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.postgres.feature.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}
