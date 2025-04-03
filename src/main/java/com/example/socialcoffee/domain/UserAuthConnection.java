package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_auth_connection")
@NoArgsConstructor
public class UserAuthConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long authId;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public UserAuthConnection(final Long userId,
                              final Long authId) {
        this.userId = userId;
        this.authId = authId;
    }
}
