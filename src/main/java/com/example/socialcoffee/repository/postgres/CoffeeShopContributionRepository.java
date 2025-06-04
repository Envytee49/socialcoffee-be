package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.CoffeeShopContribution;
import com.example.socialcoffee.domain.postgres.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoffeeShopContributionRepository extends JpaRepository<CoffeeShopContribution, Long> {
    Page<CoffeeShopContribution> findByStatusAndType(String status, String type, Pageable pageable);

    Page<CoffeeShopContribution> findByStatusAndTypeAndSubmittedByAndName(String status,
                                                                          String type,
                                                                          User user,
                                                                          String name,
                                                                          Pageable pageable);

    Page<CoffeeShopContribution> findByStatusAndTypeAndSubmittedBy(String status,
                                                                   String type,
                                                                   User user,
                                                                   PageRequest pageRequest);

    Page<CoffeeShopContribution> findByStatusAndTypeAndName(String status,
                                                            String type,
                                                            String name,
                                                            PageRequest pageRequest);

    Long countByStatus(String value);

    @Query(value = "SELECT c FROM CoffeeShopContribution c WHERE c.id = :contributionId ")
    CoffeeShopContribution findByCId(Long contributionId);
}
