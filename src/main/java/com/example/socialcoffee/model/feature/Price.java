package com.example.socialcoffee.model.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prices")
@NoArgsConstructor
public class Price extends Feature {
    public Price(String value) {
        this.value = value;
    }
}
