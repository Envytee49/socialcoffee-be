package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "notifications")
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String message;
    private String type;
    private String meta;
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
