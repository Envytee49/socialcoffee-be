package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.AuthConfig;
import com.example.socialcoffee.domain.Address;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserFollow;
import com.example.socialcoffee.dto.request.UpdateNewPassword;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.ContributorDTO;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.dto.response.UserDTO;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.AddressRepository;
import com.example.socialcoffee.repository.CoffeeShopRepository;
import com.example.socialcoffee.repository.UserFollowRepository;
import com.example.socialcoffee.repository.UserRepository;
import com.example.socialcoffee.utils.SecurityUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserFollowRepository userFollowRepository;
    private final JwtService jwtService;
    private final AuthConfig authConfig;
    private final AddressRepository addressRepository;
    private final CoffeeShopRepository coffeeShopRepository;

    public ResponseEntity<ResponseMetaData> updateNewPassword(UpdateNewPassword updateNewPassword) {
        Long userId = SecurityUtil.getUserId();
        Optional<User> optionUser = userRepository.findByUserId(userId);
        if (optionUser.isEmpty() || Status.ACTIVE.getValue().equalsIgnoreCase(optionUser.get().getStatus())) {
            log.info("User with id {} not found", userId);
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
//        if (redisAuthService.checkTimesPasswordFail(String.valueOf(userId), subSystem))
//            return ResponseEntity.badRequest().body(new ResMDLogin(
//                    new MetaDTO(MetaData.PASSWORD_FAIL_TOO_MANY_TIMES), null));
        User user = optionUser.get();
        if (!encoder.matches(updateNewPassword.getCurrentPassword(), user.getPassword())) {
            redisAuthService.countTimesPasswordFail(String.valueOf(userId));
            log.warn("Password is incorrect");
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PASSWORD_INCORRECT), null));
        }
        String oldPassword = user.getPassword();
        if (encoder.matches(updateNewPassword.getNewPassword(), oldPassword)) {
            log.warn("This password has already been used");
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PASSWORD_ALREADY_USED), null));
        }
        user.setPassword(encoder.encode(updateNewPassword.getNewPassword()));
        userRepository.save(user);
        deleteOldToken(String.valueOf(userId));
        LoginResponseDTO loginResponse = jwtService.generateAccessToken(user);
        log.info("SUCCESS while update new password with userId = {}", user.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), loginResponse));
    }

    public void deleteOldToken(String userId) {
        log.info("Start delete old token with userId = {}", userId);

        String refreshTokenKey = redisTemplate.keys(
                RedisKeyUtil.getRefreshTokenKeyByUserId(authConfig.getPrefixRedisKey(), userId));
        if (!StringUtils.isBlank(refreshTokenKey)) {
        }
        redisTemplate.delete(refreshTokenKeyList);

        String accessTokenKey = redisTemplate.keys(
                RedisKeyUtil.getAccessTokenKeyByUserId(authConfig.getPrefixRedisKey(), userId));
        if (!StringUtils.isBlank(accessTokenKey))
            redisTemplate.delete(accessTokenKeyList);
        log.info("SUCCESS delete old token with userId = {}", userId);
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> followUser(Long followerId, Long followeeId) {
        // Check if users exist
        Optional<User> optionalFollower = userRepository.findByUserId(followerId);
        if (optionalFollower.isEmpty())
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));

        Optional<User> optionalFollowee = userRepository.findById(followeeId);
        if (optionalFollowee.isEmpty())
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        // Check if already following
        UserFollow.UserFollowerId id = new UserFollow.UserFollowerId(followerId, followeeId);
        if (userFollowRepository.existsById(id)) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_FOLLOWING)));
        }

        // Create and save follow relationship
        UserFollow userFollow = new UserFollow();
        userFollow.setUserFollowerId(id);
        userFollowRepository.save(userFollow);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> unfollowUser(Long followerId, Long followeeId) {
        UserFollow.UserFollowerId id = new UserFollow.UserFollowerId(followerId, followeeId);

        if (!userFollowRepository.existsById(id)) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOLLOWING)));
        }

        userFollowRepository.deleteById(id);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public Page<UserDTO> getFollowers(Long userId, Pageable pageable) {
        // Get users who follow the specified user
        Page<User> followers = userFollowRepository.findFollowersByFolloweeId(userId, pageable);
        return followers.map(user -> user.toUserDTO(user));
    }

    public Page<UserDTO> getFollowing(Long userId, Pageable pageable) {
        // Get users who the specified user follows
        Page<User> following = userFollowRepository.findFolloweesByFollowerId(userId, pageable);
        return following.map(user -> user.toUserDTO(user));
    }

    public Page<UserDTO> search(UserSearchRequest request, Pageable pageable) {
        Page<User> users = userRepository.findByUsernameOrNameOrDisplayNameContainingIgnoreCase(request.getName(), pageable);
        return users.map(user -> user.toUserDTO(user));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        // Find the user by ID
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if(optionalUser.isEmpty()) return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        User user = optionalUser.get();
        // Update only the fields that are provided in the DTO
        if (userUpdateDTO.getDisplayName() != null) {
            user.setDisplayName(userUpdateDTO.getDisplayName());
        }

        if (userUpdateDTO.getBio() != null) {
            user.setBio(userUpdateDTO.getBio());
        }

        if (userUpdateDTO.getCoffeePreference() != null) {
            user.setCoffeePreference(userUpdateDTO.getCoffeePreference());
        }

        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }

        if (userUpdateDTO.getDob() != null) {
            user.setDob(userUpdateDTO.getDob());
        }

        if (userUpdateDTO.getGender() != null) {
            user.setGender(userUpdateDTO.getGender());
        }

        // For nested objects like address, you might want to handle them differently
        if (userUpdateDTO.getAddress() != null) {
            // Update address or create a new one if it doesn't exist
            if (user.getAddress() == null) {
                Address address = new Address();
                BeanUtils.copyProperties(userUpdateDTO.getAddress(), address);
                addressRepository.save(address);
                user.setAddress(address);
            } else {
                BeanUtils.copyProperties(userUpdateDTO.getAddress(), user.getAddress());
            }
        }

        // Save the updated user
        userRepository.save(user);

        // Convert to DTO and return
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public List<ContributorDTO> getTopContributors(int limit) {
        List<Object[]> results = coffeeShopRepository.findTopContributors(limit);

        List<ContributorDTO> contributors = new ArrayList<>();
        for (Object[] result : results) {
            Long userId = (Long) result[0];
            Long contributionCount = (Long) result[1];

            // Get the user details
            User user = userRepository.findById(userId)
                    .orElse(null);

            if (user != null) {
                ContributorDTO contributor = new ContributorDTO();
                contributor.setUser(userMapper.toUserDTO(user));
                contributor.setContributionCount(contributionCount);
                contributors.add(contributor);
            }
        }

        return contributors;
    }
}
