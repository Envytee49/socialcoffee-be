package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email and u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email,
                                        @Param("status") String status);
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByUserId(Long id);

    Page<User> findByUsernameOrNameOrDisplayNameContainingIgnoreCase(String name1, String name2, String name3, Pageable pageable);

    Optional<User> findByUsernameAndStatus(String username, String status);

    User findByIdAndStatus(Long userId, String value);

    User findByDisplayNameAndStatus(String displayName,
                                    String value);

    List<User> findByStatus(String value);
}
