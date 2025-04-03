package com.example.socialcoffee.domain.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "purposes")
@Entity
@NoArgsConstructor
public class Purpose extends Feature {
    public Purpose(String value) {
        this.value = value;
    }
}
