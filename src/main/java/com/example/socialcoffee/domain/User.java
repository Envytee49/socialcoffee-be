package com.example.socialcoffee.domain;

import com.example.socialcoffee.dto.response.FollowerDTO;
import com.example.socialcoffee.dto.response.FollowingDTO;
import com.example.socialcoffee.dto.response.UserDTO;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.FacebookUserInfo;
import com.example.socialcoffee.model.GoogleUserInfo;
import com.example.socialcoffee.utils.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String displayName;

    private String username;

    private String name;

    private String email;

    private String password;

    private String phone;

    private LocalDate dob;

    private String gender;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    private String bio;

    private String coffeePreference;

    private String status = Status.ACTIVE.getValue();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_likes",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "coffee_shop_id", referencedColumnName = "id")
    )
    private Set<CoffeeShop> likedCoffeeShops;

    private String profilePhoto;

    private String backgroundPhoto;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Collection> collections;

    @OneToMany(mappedBy = "submittedBy", cascade = CascadeType.ALL)
    private List<CoffeeShopContribution> contributions;

    public User(GoogleUserInfo info) {
        this.displayName = info.getName();
        this.username = info.getName();
        this.name = info.getName();
        this.profilePhoto = info.getPicture();
        this.email = info.getEmail();
    }

    public User(FacebookUserInfo info) {
        this.displayName = info.getName();
        this.username = info.getName();
        this.name = info.getName();
        this.profilePhoto = info.getPictureUrl();
        this.gender = info.getGender();
        this.dob = DateTimeUtil.convertStringToLocalDate(info.getBirthday());
        this.email = info.getEmail();
    }

    public void addReview(Review review) {
        if (CollectionUtils.isEmpty(this.reviews)) this.reviews = new ArrayList<>();
        this.reviews.add(review);
    }

    public void addLike(CoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.likedCoffeeShops)) this.likedCoffeeShops = new HashSet<>();
        this.likedCoffeeShops.add(coffeeShop);
    }

    public void removeLike(CoffeeShop coffeeShop) {
        if (CollectionUtils.isEmpty(this.likedCoffeeShops)) return;
        this.likedCoffeeShops.remove(coffeeShop);
    }

    public UserDTO toUserDTO() {
        return new UserDTO(this);
    }

    public FollowerDTO toFollowerDTO() {
        return new FollowerDTO(this);
    }

    public FollowingDTO toFollowingDTO() {
        return new FollowingDTO(this);
    }

    public void addNotification(String title,
                                String type,
                                String status,
                                String message,
                                String meta) {
        if (CollectionUtils.isEmpty(this.notifications)) this.notifications = new ArrayList<>();
        log.info("adding new notification");
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .status(status)
                .meta(meta)
                .user(this)
                .build();
        this.notifications.add(notification);
    }
}
