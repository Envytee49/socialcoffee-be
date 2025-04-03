package com.example.socialcoffee.repository.feature;

import com.example.socialcoffee.domain.feature.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
