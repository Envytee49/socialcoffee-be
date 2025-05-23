package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.UpdatePreferenceRequest;
import com.example.socialcoffee.dto.request.UserProfile;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.ContributionType;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.NotificationStatus;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.model.UserSettingModel;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.repository.postgres.NotificationRepository;
import com.example.socialcoffee.repository.postgres.UserFollowRepository;
import com.example.socialcoffee.repository.postgres.UserSettingRepository;
import com.example.socialcoffee.service.*;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.ObjectUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController extends BaseController {

    private final UserService userService;

    private final CloudinaryService cloudinaryService;

    private final UserFollowRepository userFollowRepository;

    private final RepoService repoService;

    private final ContributionService contributionService;

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;

    private final UserSettingRepository userSettingRepository;

    private final ReviewService reviewService;

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
    public ResponseEntity<ResponseMetaData> getProfile(@RequestParam(value = "displayName", required = false) String displayName) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        Long currentUserId = user.getId();
        user = (StringUtils.isNotBlank(displayName) && !displayName.equalsIgnoreCase(user.getDisplayName()))
                ? userRepository.findByDisplayNameAndStatus(displayName,
                Status.ACTIVE.getValue())
                : user;
        if (Objects.isNull(user)) {
            return ResponseEntity.notFound().build();
        }
        Long viewingUserId = user.getId();
        boolean isFollowing;
        if (currentUserId.equals(viewingUserId)) {
            isFollowing = false;
        } else {
            isFollowing = userFollowRepository.existsById(new UserFollow.UserFollowerId(viewingUserId,
                    currentUserId));
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                new UserProfile(user,
                        isFollowing)));
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
        Notification notification = notificationRepository.findById(id).orElseThrow(NotFoundException::new);
        notification.setStatus(NotificationStatus.READ.getValue());
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/notifications/mark-all-as-read")
    public ResponseEntity<ResponseMetaData> markAllAsRead() {
        final User currentUser = getCurrentUser();
        currentUser.getNotifications().forEach(
                n -> n.setStatus(NotificationStatus.READ.getValue())
        );
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/users/notifications/unread")
    public ResponseEntity<Long> userUnreadCount() {
        final User currentUser = getCurrentUser();
        Long count = NumberUtils.LONG_ZERO;
        final List<Notification> notifications = currentUser.getNotifications();
        if (CollectionUtils.isEmpty(notifications)) return ResponseEntity.ok().body(count);
        for (final Notification notification : notifications) {
            if (notification.getStatus().equalsIgnoreCase(NotificationStatus.UNREAD.getValue())) count++;
        }
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/users/notifications/notis")
    public ResponseEntity<ResponseMetaData> userNotifications(@Valid PageDtoIn pageDtoIn) {
        final User currentUser = getCurrentUser();
        final List<Notification> notifications = currentUser.getNotifications();
        if (CollectionUtils.isEmpty(notifications))
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                    Collections.emptyList()));
        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        List<Notification> pageResult = ObjectUtil.getPageResult(notifications,
                pageDtoIn.getPage() - 1,
                pageDtoIn.getSize());
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (final Notification notification : pageResult) {
            Object meta = ObjectUtil.stringToObject(objectMapper,
                    notification.getMeta(),
                    Object.class);
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .createdAt(DateTimeUtil.covertLocalDateToString(notification.getCreatedAt()))
                    .type(notification.getType())
                    .status(notification.getStatus())
                    .meta(meta)
                    .build();
            notificationDTOS.add(notificationDTO);
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                notificationDTOS));
    }

    @GetMapping("/users/setting")
    public ResponseEntity<ResponseMetaData> getUserSetting() {
        User user = getCurrentUser();
        Optional<UserSetting> optionalUserSetting = userSettingRepository.findById(user.getId());
        UserSettingModel userSettingModel;
        if (optionalUserSetting.isEmpty()) {
            UserSetting userSetting = new UserSetting();
            userSetting.setId(user.getId());
            userSettingModel = new UserSettingModel();
            String setting = ObjectUtil.objectToString(objectMapper,
                    userSettingModel);
            userSetting.setSetting(setting);
            userSettingRepository.save(userSetting);
        } else {
            userSettingModel = ObjectUtil.stringToObject(objectMapper,
                    optionalUserSetting.get().getSetting(), UserSettingModel.class);
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                userSettingModel));
    }

    @PutMapping("/users/setting")
    public ResponseEntity<ResponseMetaData> updateUserSetting(@RequestBody UserSettingModel userSettingModel) {
        User user = getCurrentUser();
        UserSetting userSetting = userSettingRepository.findById(user.getId()).get();
        String setting = ObjectUtil.objectToString(objectMapper, userSettingModel);
        userSetting.setSetting(setting);
        userSettingRepository.save(userSetting);
        return ResponseEntity.ok().body(new ResponseMetaData(
                new MetaDTO(MetaData.SUCCESS),
                userSettingModel
        ));
    }

    @GetMapping("/users/{displayName}/reviews")
    public ResponseEntity<ResponseMetaData> getReviewByUserId(@PathVariable(value = "displayName") String displayName,
                                                              PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) return ResponseEntity.status(401).build();
        String destinationUser = Objects.isNull(displayName) ? user.getDisplayName() : displayName;
        return reviewService.getReviewByUserId(user,
                destinationUser,
                pageDtoIn);
    }
}
