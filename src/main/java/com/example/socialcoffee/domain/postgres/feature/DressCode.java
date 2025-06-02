package com.example.socialcoffee.domain.postgres.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "dress_code")
@Entity
@NoArgsConstructor
public class DressCode extends Feature {
    public DressCode(String value) {
        this.value = value;
    }
}
