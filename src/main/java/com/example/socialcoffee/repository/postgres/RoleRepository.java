package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String value);

    Role findByName(String role);
}
