package com.example.socialcoffee.domain.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Table(name = "collections")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String coverPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany
    private Set<CoffeeShop> coffeeShops;

    public void addCoffeeShop(CoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.coffeeShops)) coffeeShops = new HashSet<>();
        coffeeShops.add(coffeeShop);
    }

    public void removeCoffeeShop(CoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.coffeeShops)) return;
        coffeeShops.remove(coffeeShop);
    }
}
