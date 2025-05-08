package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.CoffeeShopContribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeShopContributionRepository extends JpaRepository<CoffeeShopContribution, Long> {
    Page<CoffeeShopContribution> findByStatus(String status, Pageable pageable);
    Page<CoffeeShopContribution> findByType(String type, Pageable pageable);
    Page<CoffeeShopContribution> findByStatusAndType(String status, String type, Pageable pageable);
}
