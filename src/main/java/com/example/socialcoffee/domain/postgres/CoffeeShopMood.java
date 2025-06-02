package com.example.socialcoffee.domain.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "coffee_shop_mood")
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CoffeeShopMood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mood;

    private Long shopId;

    private Long userId;

    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
