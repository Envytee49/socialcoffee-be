package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
