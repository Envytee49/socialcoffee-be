package com.example.socialcoffee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "coffee_shops")
@Entity
public class CoffeeShop {

    @Id
    private Long id;
//    private String coverImage;
    private String description;
    @Column(columnDefinition = "vector(384)")
    private float[] descriptionEmbedding;

    public CoffeeShop(final Long id,
                      final String description,
                      final float[] descriptionEmbedding) {
        this.id = id;
        this.description = description;
        this.descriptionEmbedding = descriptionEmbedding;
    }

    public CoffeeShop() {

    }
    //    private String phone;
//    private String webAddress;
//    private String menuWebAddress;
//    @OneToOne
//    private Address address;

}
