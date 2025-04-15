package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
