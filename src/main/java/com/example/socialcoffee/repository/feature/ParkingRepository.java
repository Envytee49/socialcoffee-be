package com.example.socialcoffee.repository.feature;

import com.example.socialcoffee.domain.feature.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
