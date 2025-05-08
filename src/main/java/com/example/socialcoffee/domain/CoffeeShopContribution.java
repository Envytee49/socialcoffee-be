package com.example.socialcoffee.domain;

import com.example.socialcoffee.domain.feature.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    @Column(name = "contribution_id")
    private Long contributionId;

    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String additionalInfo;
    private Integer openHour;
    private Integer closeHour;
    private String type;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Image> galleryPhotos;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private String status; // PENDING, APPROVED, DECLINED
    @OneToOne
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    @OneToOne
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedBy;// Admin ID who reviewed
    private String reviewComments;

    @ManyToMany
    @JoinTable(
            name = "contribution_ambiance",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "ambiance_id")
    )
    private List<Ambiance> ambiances;

    @ManyToMany
    @JoinTable(
            name = "contribution_amenity",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @ManyToMany
    @JoinTable(
            name = "contribution_capacity",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "capacity_id")
    )
    private List<Capacity> capacities;

    @ManyToMany
    @JoinTable(
            name = "contribution_category",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "contribution_dress_code",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "dress_code_id")
    )
    private List<DressCode> dressCodes;

    @ManyToMany
    @JoinTable(
            name = "contribution_entertainment",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "entertainment_id")
    )
    private List<Entertainment> entertainments;

    @ManyToMany
    @JoinTable(
            name = "contribution_parking",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "parking_id")
    )
    private List<Parking> parkings;

    @ManyToMany
    @JoinTable(
            name = "contribution_price",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    private List<Price> prices;

    @ManyToMany
    @JoinTable(
            name = "contribution_service",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceType> serviceTypes;

    @ManyToMany
    @JoinTable(
            name = "contribution_space",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "space_id")
    )
    private List<Space> spaces;

    @ManyToMany
    @JoinTable(
            name = "contribution_specialty",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private List<Specialty> specialties;

    @ManyToMany
    @JoinTable(
            name = "contribution_visit_time",
            joinColumns = @JoinColumn(name = "contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "visit_time_id")
    )
    private List<VisitTime> visitTimes;
}