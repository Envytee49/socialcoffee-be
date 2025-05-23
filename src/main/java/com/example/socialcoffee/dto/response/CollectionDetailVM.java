package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDetailVM extends CollectionVM {
    private String description;

    private List<CoffeeShopVM> coffeeShops;

    public CollectionDetailVM(final Collection collection,
                              final List<CoffeeShopVM> coffeeShops) {
        super(collection);
        this.description = collection.getDescription();
        this.coffeeShops = coffeeShops;
    }
}
