package com.example.socialcoffee.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer rating;
    private String comment;
    @OneToMany
    private List<Image> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
}
