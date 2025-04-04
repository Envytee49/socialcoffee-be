package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.response.ResponseMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {
    public ResponseEntity<ResponseMetaData> approveCoffeeShopContribution() {

    }

    public ResponseEntity<ResponseMetaData> rejectCoffeeShopContribution() {

    }

    public ResponseEntity<ResponseMetaData> getMostContributedUsers() {

    }

    public ResponseEntity<ResponseMetaData> getCoffeeShops() {

    }
}
