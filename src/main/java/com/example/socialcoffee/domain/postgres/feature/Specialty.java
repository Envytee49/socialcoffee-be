package com.example.socialcoffee.domain.postgres.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "specialties")
@Entity
@NoArgsConstructor
public class Specialty extends Feature {
    public Specialty(String value) {
        this.value = value;
    }
}
