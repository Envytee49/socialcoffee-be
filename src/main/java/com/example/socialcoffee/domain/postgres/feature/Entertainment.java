package com.example.socialcoffee.domain.postgres.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "entertainment")
@Entity
@NoArgsConstructor
public class Entertainment extends Feature {
    public Entertainment(String value) {
        this.value = value;
    }
}
