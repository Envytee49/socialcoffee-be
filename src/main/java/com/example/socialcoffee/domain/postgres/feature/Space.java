package com.example.socialcoffee.domain.postgres.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "spaces")
@Entity
@NoArgsConstructor
public class Space extends Feature {
    public Space(String value) {
        this.value = value;
    }
}
