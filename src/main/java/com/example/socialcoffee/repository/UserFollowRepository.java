package com.example.socialcoffee.repository;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollow.UserFollowerId> {
    @Query("SELECT u FROM User u JOIN UserFollow uf ON u.id = uf.userFollowerId.followeeId WHERE uf.userFollowerId.followerId = :userId")
    Page<User> findFollowersByFolloweeId(@Param("userId") Long userId,
                                         Pageable pageable);

    @Query("SELECT uf.userFollowerId.followerId FROM UserFollow uf WHERE uf.userFollowerId IN :ids")
    Set<Long> findRelationByIdIn(List<UserFollow.UserFollowerId> ids);

    @Query("SELECT u FROM User u JOIN UserFollow uf ON u.id = uf.userFollowerId.followerId WHERE uf.userFollowerId.followeeId = :userId")
    Page<User> findFollowingsByFollowerId(@Param("userId") Long userId,
                                          Pageable pageable);
}
