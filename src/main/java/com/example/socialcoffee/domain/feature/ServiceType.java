package com.example.socialcoffee.domain.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Table(name = "services")
@Entity
@NoArgsConstructor
public class ServiceType extends Feature {
    public ServiceType(String value) {
        this.value = value;
    }
}
