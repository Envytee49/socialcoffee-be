package com.example.socialcoffee.domain.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "parking")
@Entity
@NoArgsConstructor
public class Parking extends Feature {
    public Parking(String value) {
        this.value = value;
    }
}
