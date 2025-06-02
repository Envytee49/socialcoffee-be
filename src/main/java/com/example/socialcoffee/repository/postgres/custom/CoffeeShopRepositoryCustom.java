package com.example.socialcoffee.repository.postgres.custom;

import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface CoffeeShopRepositoryCustom {

    /**
     * Searches for coffee shops based on the provided criteria with support for sorting, filtering, and pagination
     *
     * @param request The search request containing all filter, sort and pagination parameters
     *
     * @return List of matching coffee shops
     */
    Page<CoffeeShop> searchCoffeeShops(CoffeeShopSearchRequest request,
                                       Integer page,
                                       Integer size,
                                       Sort sort);
}
