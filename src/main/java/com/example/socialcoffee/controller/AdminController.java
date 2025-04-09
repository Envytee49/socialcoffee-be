package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.response.CoffeeShopDTO;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final CoffeeShopService coffeeShopService;
    private final UserService userService;

    @PutMapping("/{shopId}/approve")
    public ResponseEntity<ResponseMetaData> approveCoffeeShopContribution(@PathVariable Long shopId) {
        return coffeeShopService.updateCoffeeShopStatus(shopId,
                                                        Status.APPROVED.getValue());
    }

    @PutMapping("/{shopId}/reject")
    public ResponseEntity<ResponseMetaData> rejectCoffeeShopContribution(
            @PathVariable Long shopId) {
        return coffeeShopService.updateCoffeeShopStatus(shopId,
                                                        Status.REJECTED.getValue());
    }

    @GetMapping("/top-contributors")
    public ResponseEntity<ResponseMetaData> getMostContributedUsers(
            @RequestParam(defaultValue = "5") int limit) {

        return userService.getTopContributors(limit);
    }

    @GetMapping
    public ResponseEntity<ResponseMetaData> getCoffeeShops(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CoffeeShopDTO> coffeeShops = coffeeShopService.findCoffeeShops(name,
                                                                            status,
                                                                            PageRequest.of(page,
                                                                                           size));
        PageDtoOut<CoffeeShopDTO> response = PageDtoOut.from(page,
                                                             size,
                                                             coffeeShops.getTotalElements(),
                                                             coffeeShops.getContent());
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             response)
        );
    }
}
