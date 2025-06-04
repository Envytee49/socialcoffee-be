package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.postgres.Image;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.domain.postgres.UserFollow;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.UpdatePreferenceRequest;
import com.example.socialcoffee.dto.request.UserProfile;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.ContributionType;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.UserSettingModel;
import com.example.socialcoffee.repository.postgres.NotificationRepository;
import com.example.socialcoffee.repository.postgres.UserFollowRepository;
import com.example.socialcoffee.repository.postgres.UserSettingRepository;
import com.example.socialcoffee.service.*;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController extends BaseController {

    private final UserService userService;

    private final CloudinaryService cloudinaryService;

    private final RepoService repoService;

    private final ContributionService contributionService;

    private final ReviewService reviewService;

    private final NotificationService notificationService;

    @PutMapping("/users/profile")
    public ResponseEntity<ResponseMetaData> updateProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUserProfile(getCurrentUser(userUpdateDTO.getUserId()),
                userUpdateDTO);
    }

    @PatchMapping("/users/bio")
    public ResponseEntity<ResponseMetaData> updateBio(@RequestPart String bio) {
        User user = getCurrentUser();
        user.setBio(bio);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @GetMapping("/users/preference")
    public ResponseEntity<ResponseMetaData> getCoffeeShopPreference(@RequestParam(value = "displayName", required = false) String displayName) {
        User user = getCurrentUser(displayName);
        return userService.getUserCoffeeShopPreference(user);
    }

    @PutMapping("/users/preference")
    public ResponseEntity<ResponseMetaData> updateCoffeeShopPreference(@RequestBody UpdatePreferenceRequest request) {
        return userService.updateCoffeeShopPreference(getCurrentUser(),
                request);
    }

    @PatchMapping("/users/profile-photo")
    public ResponseEntity<ResponseMetaData> updateProfilePhoto(@RequestPart MultipartFile file) {
        User user = getCurrentUser();
        final String upload = cloudinaryService.upload(file);
        user.setProfilePhoto(upload);
        userRepository.save(user);
        NUser nUser = repoService.findNUserById(user.getId());
        nUser.setProfilePhoto(upload);
        repoService.saveNUser(nUser);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                upload));
    }

    @PatchMapping("/users/background-photo")
    public ResponseEntity<ResponseMetaData> updateBackgroundPhoto(@RequestPart MultipartFile file) {
        User user = getCurrentUser();
        final String upload = cloudinaryService.upload(file);
        user.setBackgroundPhoto(upload);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                upload));
    }

    @GetMapping("/users/search")
    public ResponseEntity<ResponseMetaData> searchUser(@Valid UserSearchRequest request,
                                                       PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1,
                pageDtoIn.getSize(),
                Sort.unsorted());
        Page<UserDTO> users = userService.search(request,
                pageable);
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                users.getTotalElements(),
                users.getContent());
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/users/photos")
    public ResponseEntity<ResponseMetaData> getUserPhotos(@RequestParam(value = "displayName") String displayName,
                                                          PageDtoIn pageDtoIn) {
        User user = getCurrentUser(displayName);
        Long userId = user.getId();
        Page<Image> followers = userService.getPhotos(userId,
                PageRequest.of(pageDtoIn.getPage() - 1,
                        pageDtoIn.getSize()));
        PageDtoOut<Image> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                followers.getTotalElements(),
                followers.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/users/recent-photos")
    public ResponseEntity<ResponseMetaData> getUserPhotos(@RequestParam(value = "displayName") String displayName) {
        User user = getCurrentUser(displayName);
        Long userId = user.getId();
        return userService.getRecentPhotos(userId);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<ResponseMetaData> getUserProfile(@RequestParam(value = "displayName", required = false) String displayName) {
        User user = getCurrentUser();
        return userService.getUserProfile(user, displayName);
    }

    @PostMapping("/users/{followingWhoId}/follow")
    public ResponseEntity<ResponseMetaData> followUser(@PathVariable Long followingWhoId) {
        User user = getCurrentUser();
        return userService.followUser(user,
                followingWhoId);
    }

    @PostMapping("/users/{unfollowingWhoId}/unfollow")
    public ResponseEntity<ResponseMetaData> unfollowUser(@PathVariable Long unfollowingWhoId) {
        User user = getCurrentUser();
        return userService.unfollowUser(user,
                unfollowingWhoId);
    }

    @GetMapping("/users/followers")
    public ResponseEntity<ResponseMetaData> getFollowers(@RequestParam(value = "displayName", required = false) String displayName,
                                                         PageDtoIn pageDtoIn) {
        User user = getCurrentUser(displayName);
        Page<FollowerDTO> followers = userService.getFollowers(user.getId(),
                PageRequest.of(pageDtoIn.getPage() - 1,
                        pageDtoIn.getSize()));
        PageDtoOut<FollowerDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                followers.getTotalElements(),
                followers.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/users/recent-followers")
    public ResponseEntity<ResponseMetaData> getFollowers(@RequestParam(value = "displayName", required = false) String displayName) {
        User user = getCurrentUser(displayName);
        return userService.getRecentFollowers(user.getId());
    }

    @GetMapping("/users/following")
    public ResponseEntity<ResponseMetaData> getFollowing(@RequestParam(value = "displayName", required = false) String displayName,
                                                         PageDtoIn pageDtoIn) {
        User user = getCurrentUser(displayName);
        Page<FollowingDTO> following = userService.getFollowing(user.getId(),
                PageRequest.of(pageDtoIn.getPage() - 1,
                        pageDtoIn.getSize()));
        PageDtoOut<FollowingDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                following.getTotalElements(),
                following.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/users/recent-following")
    public ResponseEntity<ResponseMetaData> getFollowing(@RequestParam(value = "displayName", required = false) String displayName) {
        User user = getCurrentUser(displayName);
        return userService.getRecentFollowing(user.getId());
    }

    @PostMapping("/users/migrate/neo4j")
    public ResponseEntity<ResponseMetaData> migrateUser() {
        return userService.migrateUsers();
    }

    @GetMapping("/users/contributions")
    public ResponseEntity<ResponseMetaData> getContribution(@RequestParam(value = "name", required = false) String name,
                                                            @RequestParam(value = "status", required = false) String status,
                                                            @RequestParam(value = "type") String type,
                                                            PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        return userService.getContributions(user,
                name,
                status,
                type,
                pageDtoIn);
    }

    @GetMapping("/users/requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseMetaData> getRequests(
            @RequestParam(required = false) String name,
            @RequestParam String status,
            @RequestParam ContributionType type,
            PageDtoIn pageDtoIn) {
        PageRequest pageRequest = PageRequest.of(pageDtoIn.getPage() - 1,
                pageDtoIn.getSize());
        return contributionService.getContributions(SecurityUtil.getUserId(),
                name,
                status,
                type.getValue(),
                pageRequest);
    }

    @PutMapping("/users/notifications/{id}")
    public ResponseEntity<ResponseMetaData> markAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/notifications/mark-all-as-read")
    public ResponseEntity<ResponseMetaData> markAllAsRead() {
        final User currentUser = getCurrentUser();
        notificationService.markAllNotificationsAsRead(currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/notifications/unread")
    public ResponseEntity<Long> userUnreadCount() {
        final User currentUser = getCurrentUser();
        return notificationService.getUnreadNotifications(currentUser);
    }

    @GetMapping("/users/notifications/notis")
    public ResponseEntity<ResponseMetaData> userNotifications(@Valid PageDtoIn pageDtoIn) {
        final User user = getCurrentUser();
        return notificationService.getUserNotifications(user, pageDtoIn);
    }

    @GetMapping("/users/setting")
    public ResponseEntity<ResponseMetaData> getUserSetting() {
        return userService.getUserSetting();
    }

    @PutMapping("/users/setting")
    public ResponseEntity<ResponseMetaData> updateUserSetting(@RequestBody UserSettingModel userSettingModel) {
        return userService.updateUserSetting(userSettingModel);
    }

    @GetMapping("/users/{displayName}/reviews")
    public ResponseEntity<ResponseMetaData> getReviewByUserId(@PathVariable(value = "displayName") String displayName,
                                                              PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        String destinationUser = Objects.isNull(displayName) ? user.getDisplayName() : displayName;
        return reviewService.getReviewByUserId(user,
                destinationUser,
                pageDtoIn);
    }

}
