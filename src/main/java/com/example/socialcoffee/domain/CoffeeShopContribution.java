package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_shop_contributions")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CoffeeShopContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String contribution;
    private String reviewComments;
    private String type;
    private String status;
    @ManyToOne
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @OneToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    @ManyToOne
    @JoinColumn(name = "coffee_shop_id")
    private CoffeeShop coffeeShop;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
