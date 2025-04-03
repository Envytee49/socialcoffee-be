package com.example.socialcoffee.repository.feature;

import com.example.socialcoffee.domain.feature.Capacity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapacityRepository extends JpaRepository<Capacity, Long> {
}
