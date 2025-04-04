package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.response.CoffeeShopDTO;
import com.example.socialcoffee.dto.response.ContributorDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final CoffeeShopService coffeeShopService;
    private final UserService userService;

    @PutMapping("/{shopId}/approve")
    public ResponseEntity<ResponseMetaData> approveCoffeeShopContribution(@PathVariable Long shopId) {
        coffeeShopService.updateCoffeeShopStatus(shopId, Status.APPROVED.getValue());
        return ResponseEntity.ok(new ResponseMetaData(true, "Coffee shop contribution approved successfully"));
    }

    @PutMapping("/{shopId}/reject")
    public ResponseEntity<ResponseMetaData> rejectCoffeeShopContribution(
            @PathVariable Long shopId) {

        coffeeShopService.updateCoffeeShopStatus(shopId, Status.REJECTED.getValue());
        return ResponseEntity.ok(new ResponseMetaData(true, "Coffee shop contribution rejected successfully"));
    }

    @GetMapping("/top-contributors")
    public ResponseEntity<ResponseMetaData> getMostContributedUsers(
            @RequestParam(defaultValue = "5") int limit) {

        List<ContributorDTO> topContributors = userService.getTopContributors(limit);

        return ResponseEntity.ok(new ResponseMetaData(
                true,
                "Top coffee shop contributors retrieved successfully",
                topContributors
        ));
    }

    @GetMapping
    public ResponseEntity<ResponseMetaData> getCoffeeShops(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CoffeeShopDTO> coffeeShops = coffeeShopService.findCoffeeShops(name, status, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("coffeeShops", coffeeShops.getContent());
        response.put("currentPage", coffeeShops.getNumber());
        response.put("totalItems", coffeeShops.getTotalElements());
        response.put("totalPages", coffeeShops.getTotalPages());

        return ResponseEntity.ok(new ResponseMetaData(
                true,
                "Coffee shops retrieved successfully",
                response
        ));
    }
}
