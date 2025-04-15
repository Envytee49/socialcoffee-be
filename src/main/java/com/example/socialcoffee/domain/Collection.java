package com.example.socialcoffee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Table(name = "collections")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String privacy;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToMany
    private Set<CoffeeShop> coffeeShops;

    public void addCoffeeShop(CoffeeShop coffeeShop) {
        if(CollectionUtils.isEmpty(this.coffeeShops)) coffeeShops = new HashSet<>();
        coffeeShops.add(coffeeShop);
    }
}
