package com.example.socialcoffee.domain;

import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.response.CoffeeShopDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Table(name = "coffee_shops")
@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CoffeeShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String additionInfo;
    private Boolean isSponsored = Boolean.FALSE;
    private Integer openHour;
    private Integer closeHour;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Image> galleryPhotos;
    @OneToOne
    private Address address;
    @ManyToMany(mappedBy = "likedCoffeeShops", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    private String status;
    private Long createdBy;
    @ManyToMany
    @JoinTable(
            name = "coffee_shop_ambiance",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "ambiance_id")
    )
    private List<Ambiance> ambiances;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_amenity",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_capacity",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "capacity_id")
    )
    private List<Capacity> capacities;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_category",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_dress_code",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "dress_code_id")
    )
    private List<DressCode> dressCodes;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_purposes",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "purpose_id")
    )
    private List<Purpose> purposes;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_entertainment",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "entertainment_id")
    )
    private List<Entertainment> entertainments;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_parking",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "parking_id")
    )
    private List<Parking> parkings;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_price",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    private List<Price> prices;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_service",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceType> serviceTypes;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_space",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "space_id")
    )
    private List<Space> spaces;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_specialty",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private List<Specialty> specialties;

    @ManyToMany
    @JoinTable(
            name = "coffee_shop_visit_time",
            joinColumns = @JoinColumn(name = "coffee_shop_id"),
            inverseJoinColumns = @JoinColumn(name = "visit_time_id")
    )
    private List<VisitTime> visitTimes;

    @Formula("(select avg(r.rating) from reviews r where r.coffee_shop_id = id)")
    private Double averageRating = 0.0;

    @Formula("(select count(r.id) from reviews r where r.coffee_shop_id = id)")
    private Long reviewCount = 0L;

    @OneToMany(mappedBy = "coffeeShop", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    public void addReview(Review review) {
        if (CollectionUtils.isEmpty(this.reviews)) this.reviews = new ArrayList<>();
        this.reviews.add(review);
    }

    public void updateGalleryPhotos(List<Image> galleryPhotos) {
        this.galleryPhotos.addAll(0, galleryPhotos);
    }


    public CoffeeShopDTO toCoffeeShopDTO() {
        CoffeeShopDTO coffeeShopDTO = new CoffeeShopDTO();
        BeanUtils.copyProperties(this, coffeeShopDTO);
        return coffeeShopDTO;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeShop that = (CoffeeShop) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return "CoffeeShop{" +
                "name='" + name + '\'' +
                ", coverPhoto='" + coverPhoto + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", webAddress='" + webAddress + '\'' +
                ", menuWebAddress='" + menuWebAddress + '\'' +
                ", additionInfo='" + additionInfo + '\'' +
                ", openHour=" + openHour +
                ", closeHour=" + closeHour +
                ", galleryPhotos=" + galleryPhotos +
                ", address=" + address +
                '}';
    }

    @JsonIgnore
    public String getOverviewAddress() {
        final Address addr = this.address;
        List<String> parts = new ArrayList<>();

        if (StringUtils.isNotBlank(addr.getProvince())) {
            parts.add(addr.getProvince());
        }
        if (StringUtils.isNotBlank(addr.getDistrict())) {
            parts.add(addr.getDistrict());
        }
        if (StringUtils.isNotBlank(addr.getWard())) {
            parts.add(addr.getWard());
        }

        return String.join(", ", parts);
    }
}
