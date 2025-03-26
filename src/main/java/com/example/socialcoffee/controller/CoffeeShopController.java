package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.CoffeeShopService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/coffee-shops")
@RequiredArgsConstructor
public class CoffeeShopController {

    private final CoffeeShopService coffeeShopService;

    @GetMapping("/recommendation")
    public ResponseEntity<ResponseMetaData> getRecommendation(@RequestParam String prompt) {
        return coffeeShopService.getRecommendation(prompt);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseMetaData> createCoffeeShop(@ModelAttribute CreateCoffeeShopRequest request) {
        return coffeeShopService.createCoffeeShop(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMetaData> getCoffeeShopById(@PathVariable Long id) {
        return coffeeShopService.getCoffeeShopById(id);
    }


    @GetMapping("")
    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(
            Pageable pageable
    ) {
        return coffeeShopService.getAllCoffeeShop(pageable);
    }

    @GetMapping("/search-filters")
    public ResponseEntity<ResponseMetaData> getSearchFilters() {
        return coffeeShopService.getSearchFilters();
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMetaData> searchCoffeeShop(CoffeeShopSearchRequest request, Pageable pageable) {
        return coffeeShopService.search(request, pageable);
    }
}
