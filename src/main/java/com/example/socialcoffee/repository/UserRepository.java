package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(final String email,
                                        final int ordinal);
}
