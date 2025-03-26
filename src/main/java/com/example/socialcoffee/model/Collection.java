package com.example.socialcoffee.model;

import jakarta.persistence.*;

import java.util.List;

@Table(name = "collections")
@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String privacy;
    @ManyToMany
    private List<CoffeeShop> coffeeShops;
}
