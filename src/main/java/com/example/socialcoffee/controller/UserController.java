package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.UpdateNewPassword;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.dto.response.UserDTO;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.service.UserService;
import com.example.socialcoffee.service.ValidationService;
import com.example.socialcoffee.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserService userService;
    private final ValidationService validationService;

    public ResponseEntity<ResponseMetaData> updateProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        // Get the current authenticated user's ID
        Long userId = SecurityUtil.getUserId();
        return userService.updateUserProfile(userId, userUpdateDTO);
    }

    @GetMapping("/users/search")
    public ResponseEntity<ResponseMetaData> searchUser(UserSearchRequest request, PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getSize(),
                Sort.unsorted());
        Page<UserDTO> users = userService.search(request, pageable);
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                users.getTotalElements(),
                users.getContent());
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), pageDtoOut));
    }
    @PutMapping("/users/update-password")
    public ResponseEntity<ResponseMetaData> updatePassword(@RequestBody UpdateNewPassword updateNewPassword) {
        List<MetaDTO> metaList = validationService.validateUpdateNewPassword(updateNewPassword);
        if (!CollectionUtils.isEmpty(metaList))
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaList));

        return userService.updateNewPassword(updateNewPassword);
    }

    @PostMapping("/users/{followeeId}/follow")
    public ResponseEntity<ResponseMetaData> followUser(@PathVariable Long followeeId) {
        Long followerId = SecurityUtil.getUserId();
        return userService.followUser(followerId, followeeId);
    }

    @PostMapping("/users/{followeeId}/unfollow")
    public ResponseEntity<ResponseMetaData> unfollowUser(@PathVariable Long followeeId) {
        Long followerId = SecurityUtil.getUserId();
        return userService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<ResponseMetaData> getFollowers(@PathVariable Long userId,
                                                         PageDtoIn pageDtoIn) {
        Page<UserDTO> followers = userService.getFollowers(userId, PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getSize()));
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                followers.getTotalElements(),
                followers.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), pageDtoOut));
    }

    @GetMapping("/users/{userId}/following")
    public ResponseEntity<ResponseMetaData> getFollowing(@PathVariable Long userId,
                                                         PageDtoIn pageDtoIn) {
        Page<UserDTO> following = userService.getFollowing(userId, PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getSize()));
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                following.getTotalElements(),
                following.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), pageDtoOut));
    }


    public ResponseEntity<ResponseMetaData> getUserReview() {

    }

    public ResponseEntity<ResponseMetaData> reactUserReview() {

    }
}
