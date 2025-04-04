package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.response.ResponseMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    public ResponseEntity<ResponseMetaData> updateProfile() {

    }

    public ResponseEntity<ResponseMetaData> changePassword() {

    }

    public ResponseEntity<ResponseMetaData> followUser() {

    }

    public ResponseEntity<ResponseMetaData> unfollowUser() {

    }

    public ResponseEntity<ResponseMetaData> getUserReview() {

    }

    public ResponseEntity<ResponseMetaData> reactUserUser() {

    }

    public ResponseEntity<ResponseMetaData> getFollowers() {

    }

    public ResponseEntity<ResponseMetaData> getFollowing() {

    }
}
