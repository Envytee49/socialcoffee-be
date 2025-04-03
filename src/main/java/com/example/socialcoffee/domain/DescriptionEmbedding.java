package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "description_embedding")
@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DescriptionEmbedding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "description_embedding", columnDefinition = "vector(384)")
    private Float[] descriptionEmbedding;

    @OneToOne(fetch = FetchType.LAZY)
    private CoffeeShop coffeeShop;

}
