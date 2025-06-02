package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.User;
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

    boolean existsByUsername(String username);

    boolean existsByDisplayName(String displayName);

    @Query(value = "SELECT COUNT(*) > 0 FROM users u JOIN users_likes ul ON u.id = :userId AND u.id = ul.user_id", nativeQuery = true)
    boolean existsUserLike(@Param("userId") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM users u JOIN user_follows ul ON u.id = :userId AND u.id = ul.followee_id", nativeQuery = true)
    boolean existsUserFollow(@Param("userId") Long id);
}
