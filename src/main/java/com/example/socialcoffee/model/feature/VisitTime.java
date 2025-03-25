package com.example.socialcoffee.model.feature;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visit_time")
@NoArgsConstructor
public class VisitTime extends Feature {
    public VisitTime(String value) {
        this.value = value;
    }
}
