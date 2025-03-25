package com.example.socialcoffee.repository.feature;

import com.example.socialcoffee.model.feature.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
