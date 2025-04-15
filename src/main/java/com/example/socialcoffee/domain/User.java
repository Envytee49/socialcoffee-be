package com.example.socialcoffee.domain;

import com.example.socialcoffee.dto.response.UserDTO;
import com.example.socialcoffee.mapper.UserMapper;
import com.example.socialcoffee.model.FacebookUserInfo;
import com.example.socialcoffee.model.GoogleUserInfo;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.utils.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @OneToOne
    private Address address;
    private String bio;
    private String coffeePreference;
    private String status = Status.ACTIVE.getValue();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles;
    private String profilePhoto;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    @OneToMany
    private List<Review> reviews;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Collection> collections;

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
        this.reviews.add(review);
    }

    public UserDTO toUserDTO() {
        return new UserDTO(this);
    }
}
