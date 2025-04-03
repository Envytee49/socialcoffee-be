package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email and u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email,
                                        @Param("status") String status);
}
