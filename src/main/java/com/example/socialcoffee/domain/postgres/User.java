package com.example.socialcoffee.domain.postgres;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "name")
    private String fullName;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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
        this.fullName = info.getName();
        this.profilePhoto = info.getPicture();
        this.email = info.getEmail();
    }

    public User(FacebookUserInfo info) {
        this.displayName = info.getName();
        this.username = info.getName();
        this.fullName = info.getName();
        this.profilePhoto = info.getPictureUrl();
        this.gender = info.getGender();
        this.dob = DateTimeUtil.convertStringToLocalDate(info.getBirthday());
        this.email = info.getEmail();
    }

    public UserDTO toUserDTO() {
        return new UserDTO(this);
    }
}
