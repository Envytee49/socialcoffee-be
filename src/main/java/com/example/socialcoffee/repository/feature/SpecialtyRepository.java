package com.example.socialcoffee.repository.feature;

import com.example.socialcoffee.domain.feature.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
