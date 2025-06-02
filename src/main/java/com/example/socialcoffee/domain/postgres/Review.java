package com.example.socialcoffee.domain.postgres;

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

    private Integer rating;

    private String comment;

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

    public Review(Integer rating, String comment, List<Image> images,
                  final User user,
                  final CoffeeShop coffeeShop) {
        this.rating = rating;
        this.comment = comment;
        this.images = images;
        this.user = user;
        this.coffeeShop = coffeeShop;
    }

    public void addImages(List<Image> newImages) {
        this.images.addAll(newImages);
    }
}
