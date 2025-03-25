package com.example.socialcoffee.model.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "amenities")
@Entity
@NoArgsConstructor
public class Amenity extends Feature {
    public Amenity(String value) {
        this.value = value;
    }
}
