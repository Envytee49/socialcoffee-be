package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String value);

    Role findByName(String role);
}
