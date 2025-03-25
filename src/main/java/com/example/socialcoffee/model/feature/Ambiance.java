package com.example.socialcoffee.model.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ambiances")
@NoArgsConstructor
public class Ambiance extends Feature {
    public Ambiance(String value) {
        this.value = value;
    }
}
