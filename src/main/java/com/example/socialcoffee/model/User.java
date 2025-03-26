package com.example.socialcoffee.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    @OneToOne
    private Address address;
    private String bio;
    private String coffeePreference;
    private String status;
    @ManyToMany
    private List<Role> roles;
    private String profilePhoto;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    @OneToMany
    private List<Review> reviews;
    @OneToMany
    private List<Collection> collections;
}
