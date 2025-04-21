package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserFollow;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CollectionRequest;
import com.example.socialcoffee.dto.request.UserProfile;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.dto.response.UserDTO;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.UserFollowRepository;
import com.example.socialcoffee.service.CloudinaryService;
import com.example.socialcoffee.service.UserService;
import com.example.socialcoffee.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController extends BaseController {

    private final UserService userService;
    private final ValidationService validationService;
    private final CloudinaryService cloudinaryService;
    private final UserFollowRepository userFollowRepository;

    @PutMapping("/users/profile")
    public ResponseEntity<ResponseMetaData> updateProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        user = (Objects.nonNull(userUpdateDTO.getUserId()) && !Objects.equals(user.getId(), userUpdateDTO.getUserId()))
                ? userRepository.findByIdAndStatus(userUpdateDTO.getUserId(),
                                                            Status.ACTIVE.getValue())
                : user;
        if(Objects.isNull(user))
            return ResponseEntity.notFound().build();

        return userService.updateUserProfile(user,
                                             userUpdateDTO);
    }

    @PatchMapping("/users/bio")
    public ResponseEntity<ResponseMetaData> updateBio(@RequestPart String bio) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        user.setBio(bio);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @PatchMapping("/users/profile-photo")
    public ResponseEntity<ResponseMetaData> updateProfilePhoto(@RequestPart MultipartFile file) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        final String upload = cloudinaryService.upload(file);
        user.setProfilePhoto(upload);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             upload));
    }

    @PatchMapping("/users/background-photo")
    public ResponseEntity<ResponseMetaData> updateBackgroundPhoto(@RequestPart MultipartFile file) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        final String upload = cloudinaryService.upload(file);
        user.setBackgroundPhoto(upload);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             upload));
    }

    @GetMapping("/users/search")
    public ResponseEntity<ResponseMetaData> searchUser(UserSearchRequest request,
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

    //    @PutMapping("/users/update-password")
//    public ResponseEntity<ResponseMetaData> updatePassword(@RequestBody UpdateNewPassword updateNewPassword) {
//        List<MetaDTO> metaList = validationService.validateUpdateNewPassword(updateNewPassword);
//        if (!CollectionUtils.isEmpty(metaList))
//            return ResponseEntity.badRequest().body(new ResponseMetaData(metaList));
//
//        return userService.updateNewPassword(updateNewPassword);
//    }
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
        if(Objects.isNull(user)) {
            return ResponseEntity.notFound().build();
        }
        Long viewingUserId = user.getId();
        boolean  isFollowing;
        if(currentUserId.equals(viewingUserId)) {
            isFollowing = false;
        } else {
            isFollowing = userFollowRepository.existsById(new UserFollow.UserFollowerId(currentUserId,
                                                                                        viewingUserId));
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             new UserProfile(user, isFollowing)));
    }

    @PostMapping("/users/{followeeId}/follow")
    public ResponseEntity<ResponseMetaData> followUser(@PathVariable Long followeeId) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        return userService.followUser(user,
                                      followeeId);
    }

    @PostMapping("/users/{followeeId}/unfollow")
    public ResponseEntity<ResponseMetaData> unfollowUser(@PathVariable Long followeeId) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        return userService.unfollowUser(user,
                                        followeeId);
    }

    @GetMapping("/users/followers")
    public ResponseEntity<ResponseMetaData> getFollowers(@RequestParam(value = "displayName", required = false) String displayName,
                                                         PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        user = (StringUtils.isNotBlank(displayName) && !displayName.equalsIgnoreCase(user.getDisplayName()))
                ? userRepository.findByDisplayNameAndStatus(displayName,
                                                            Status.ACTIVE.getValue())
                : user;
        if(Objects.isNull(user)) {
            return ResponseEntity.notFound().build();
        }
        Page<UserDTO> followers = userService.getFollowers(user.getId(),
                                                           PageRequest.of(pageDtoIn.getPage() - 1,
                                                                          pageDtoIn.getSize()));
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                                                         pageDtoIn.getSize(),
                                                         followers.getTotalElements(),
                                                         followers.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      pageDtoOut));

    }

    @GetMapping("/users/following")
    public ResponseEntity<ResponseMetaData> getFollowing(@RequestParam(value = "displayName", required = false) String displayName,
                                                         PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        user = (StringUtils.isNotBlank(displayName) && !displayName.equalsIgnoreCase(user.getDisplayName()))
                ? userRepository.findByDisplayNameAndStatus(displayName,
                                                            Status.ACTIVE.getValue())
                : user;
        if(Objects.isNull(user)) {
            return ResponseEntity.notFound().build();
        }
        Page<UserDTO> following = userService.getFollowing(user.getId(),
                                                           PageRequest.of(pageDtoIn.getPage() - 1,
                                                                          pageDtoIn.getSize()));
        PageDtoOut<UserDTO> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                                                         pageDtoIn.getSize(),
                                                         following.getTotalElements(),
                                                         following.getContent());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      pageDtoOut));
    }

    @GetMapping("/users/{userId}/collections")
    public ResponseEntity<ResponseMetaData> getCollections(@RequestParam(value = "user_id", required = false) Long userId,
                                                           PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        Long destinationUserId = Objects.isNull(userId) ? user.getId() : userId;
        return userService.getCollections(destinationUserId,
                                          pageDtoIn);
    }

    @GetMapping("/users/collections/{collectionId}")
    public ResponseEntity<ResponseMetaData> getCollection(@RequestParam(value = "user_id", required = false) Long userId,
                                                          @PathVariable Long collectionId) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        Long destinationUserId = Objects.isNull(userId) ? user.getId() : userId;
        return userService.getCollectionById(destinationUserId,
                                             collectionId);
    }

    @PostMapping("/users/collections")
    public ResponseEntity<ResponseMetaData> createNewCollection(@Valid @RequestBody CollectionRequest request) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        return userService.createNewCollection(user,
                                               request);
    }

    @PutMapping("/users/collections/{collectionId}")
    public ResponseEntity<ResponseMetaData> addCoffeeShopToCollection(@PathVariable Long collectionId,
                                                                      @RequestPart(value = "shopId") String shopId) {
        return userService.addCoffeeShopToCollection(collectionId,
                                                     Long.parseLong(shopId));
    }

//    public ResponseEntity<ResponseMetaData> getUserReview() {
//
//    }
//
//    public ResponseEntity<ResponseMetaData> reactUserReview() {
//
//    }
}
