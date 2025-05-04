package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.feature.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
