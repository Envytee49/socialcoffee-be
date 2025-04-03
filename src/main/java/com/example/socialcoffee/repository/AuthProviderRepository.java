package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    boolean existsByName(String value);

    AuthProvider findByName(String authProvider);
}
