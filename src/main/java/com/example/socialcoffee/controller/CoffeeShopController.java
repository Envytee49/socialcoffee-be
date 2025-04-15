package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.CoffeeShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
public class CoffeeShopController extends BaseController{

    private final CoffeeShopService coffeeShopService;

    @GetMapping("/coffee-shops/recommendation")
    public ResponseEntity<ResponseMetaData> getRecommendation(@RequestParam String prompt) {
        return coffeeShopService.getRecommendation(prompt);
    }

    @PostMapping(value = "/coffee-shops", consumes = "multipart/form-data")
    public ResponseEntity<ResponseMetaData> createCoffeeShop(@ModelAttribute CreateCoffeeShopRequest request) {
        User user = getCurrentUser();
        if(Objects.isNull(user))
            return ResponseEntity.status(401).build();
        return coffeeShopService.createCoffeeShop(user, request);
    }

    @GetMapping("/coffee-shops/{id}")
    public ResponseEntity<ResponseMetaData> getCoffeeShopById(@PathVariable Long id) {
        return coffeeShopService.getCoffeeShopById(id);
    }


    @GetMapping("/coffee-shops")
    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(
            Pageable pageable
    ) {
        return coffeeShopService.getAllCoffeeShop(pageable);
    }

    @GetMapping("/coffee-shops/search-filters")
    public ResponseEntity<ResponseMetaData> getSearchFilters() {
        return coffeeShopService.getSearchFilters();
    }

    @GetMapping("/coffee-shops/search")
    public ResponseEntity<ResponseMetaData> searchCoffeeShop(CoffeeShopSearchRequest request, PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getSize(),
                                           Sort.unsorted());
        return coffeeShopService.search(request, pageable);
    }
}
