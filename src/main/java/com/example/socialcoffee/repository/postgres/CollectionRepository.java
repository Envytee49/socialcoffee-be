package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.Collection;
import com.example.socialcoffee.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user, Pageable pageable);
}
