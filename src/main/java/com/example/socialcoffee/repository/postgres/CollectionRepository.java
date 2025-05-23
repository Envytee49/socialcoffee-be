package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser_Id(Long userId, Pageable pageable);
}
