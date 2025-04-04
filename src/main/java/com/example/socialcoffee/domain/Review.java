package com.example.socialcoffee.domain;

import com.example.socialcoffee.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
@Setter
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer rating;
    private String comment;
    private Boolean isAnonymous;
    @OneToMany
    private List<Image> images;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String status = Status.ACTIVE.getValue();
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private CoffeeShop coffeeShop;
    public Review(Integer rating, String title, String comment, Boolean isAnonymous, List<Image> images) {
        this.rating = rating;
        this.title = title;
        this.isAnonymous = isAnonymous;
        this.comment = comment;
        this.images = images;
    }
}
