package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.Collection;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user, Pageable pageable);
}
