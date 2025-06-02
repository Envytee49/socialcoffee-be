package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    boolean existsByName(String value);

    AuthProvider findByName(String authProvider);
}
