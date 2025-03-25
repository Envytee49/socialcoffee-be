package com.example.socialcoffee.model.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "capacities")
@Entity
@NoArgsConstructor
public class Capacity extends Feature {
    public Capacity(String value) {
        this.value = value;
    }
}
