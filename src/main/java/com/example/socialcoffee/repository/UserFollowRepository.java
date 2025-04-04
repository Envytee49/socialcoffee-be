package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollow.UserFollowerId> {
    @Query("SELECT u FROM User u JOIN UserFollow uf ON u.id = uf.userFollowerId.followerId WHERE uf.userFollowerId.followeeId = :userId")
    Page<User> findFollowersByFolloweeId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u FROM User u JOIN UserFollow uf ON u.id = uf.userFollowerId.followeeId WHERE uf.userFollowerId.followerId = :userId")
    Page<User> findFolloweesByFollowerId(@Param("userId") Long userId, Pageable pageable);
}
