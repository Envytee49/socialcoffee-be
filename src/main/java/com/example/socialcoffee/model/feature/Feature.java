package com.example.socialcoffee.model.feature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@MappedSuperclass
@Setter
@Getter
public abstract class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String value;
    @JsonIgnore
    public String getName() {
        return value;
    }
}
