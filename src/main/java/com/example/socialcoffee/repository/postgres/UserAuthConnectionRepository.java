package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.UserAuthConnection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthConnectionRepository extends JpaRepository<UserAuthConnection, Long> {
}
