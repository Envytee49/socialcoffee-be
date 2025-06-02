package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionVM {
    private Long id;

    private String name;

    private String coverUrl;

    private Integer totalItem;

    private Boolean isSaved;

    public CollectionVM(Collection collection, CoffeeShop coffeeShop) {
        this.id = collection.getId();
        this.name = collection.getName();
        this.coverUrl = collection.getCoverPhoto();
        this.totalItem = collection.getCoffeeShops().size();
        if (Objects.nonNull(coffeeShop)) {
            this.isSaved = collection.getCoffeeShops().contains(coffeeShop);
        }
    }

    public CollectionVM(Collection collection) {
        this.id = collection.getId();
        this.name = collection.getName();
        this.coverUrl = collection.getCoverPhoto();
        this.totalItem = collection.getCoffeeShops().size();
    }
}
