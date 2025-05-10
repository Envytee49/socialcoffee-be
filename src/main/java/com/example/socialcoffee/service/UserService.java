package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.UserFollow;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.request.UpdatePreferenceRequest;
import com.example.socialcoffee.dto.request.UserProfile;
import com.example.socialcoffee.dto.request.UserSearchRequest;
import com.example.socialcoffee.dto.request.UserUpdateDTO;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.neo4j.feature.*;
import com.example.socialcoffee.neo4j.relationship.Prefer;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.ReviewRepository;
import com.example.socialcoffee.repository.postgres.UserFollowRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserFollowRepository userFollowRepository;
    private final CoffeeShopRepository coffeeShopRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CacheableService cacheableService;
    private final ObjectMapper objectMapper;
    private final RepoService repoService;

    //    public ResponseEntity<ResponseMetaData> updateNewPassword(UpdateNewPassword updateNewPassword) {
//        Long userId = SecurityUtil.getUserId();
//        Optional<User> optionUser = userRepository.findByUserId(userId);
//        if (optionUser.isEmpty() || Status.ACTIVE.getValue().equalsIgnoreCase(optionUser.get().getStatus())) {
//            log.info("User with id {} not found", userId);
//            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
//        }
////        if (redisAuthService.checkTimesPasswordFail(String.valueOf(userId), subSystem))
////            return ResponseEntity.badRequest().body(new ResMDLogin(
////                    new MetaDTO(MetaData.PASSWORD_FAIL_TOO_MANY_TIMES), null));
//        User user = optionUser.get();
//        if (!encoder.matches(updateNewPassword.getCurrentPassword(), user.getPassword())) {
//            redisAuthService.countTimesPasswordFail(String.valueOf(userId));
//            log.warn("Password is incorrect");
//            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PASSWORD_INCORRECT), null));
//        }
//        String oldPassword = user.getPassword();
//        if (encoder.matches(updateNewPassword.getNewPassword(), oldPassword)) {
//            log.warn("This password has already been used");
//            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PASSWORD_ALREADY_USED), null));
//        }
//        user.setPassword(encoder.encode(updateNewPassword.getNewPassword()));
//        userRepository.save(user);
//        deleteOldToken(String.valueOf(userId));
//        LoginResponseDTO loginResponse = jwtService.generateAccessToken(user);
//        log.info("SUCCESS while update new password with userId = {}", user.getId());
//        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), loginResponse));
//    }
//
//    public void deleteOldToken(String userId) {
//        log.info("Start delete old token with userId = {}", userId);
//
//        String refreshTokenKey = redisTemplate.keys(
//                RedisKeyUtil.getRefreshTokenKeyByUserId(authConfig.getPrefixRedisKey(), userId));
//        if (!StringUtils.isBlank(refreshTokenKey)) {
//        }
//        redisTemplate.delete(refreshTokenKeyList);
//
//        String accessTokenKey = redisTemplate.keys(
//                RedisKeyUtil.getAccessTokenKeyByUserId(authConfig.getPrefixRedisKey(), userId));
//        if (!StringUtils.isBlank(accessTokenKey))
//            redisTemplate.delete(accessTokenKeyList);
//        log.info("SUCCESS delete old token with userId = {}", userId);
//    }
    public ResponseEntity<ResponseMetaData> getProfile(User user) {

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             new UserProfile(user)));
    }

    public ResponseEntity<ResponseMetaData> getProfileByName(User user) {

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             new UserProfile(user)));
    }

    public ResponseEntity<ResponseMetaData> getUserCoffeeShopPreference(User user) {
        try {
            SearchFilter preference = objectMapper.readValue(
                    user.getCoffeePreference(),
                    new TypeReference<>() {
                    }
            );
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), preference));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public ResponseEntity<ResponseMetaData> updateCoffeeShopPreference(User user,
                                                                       UpdatePreferenceRequest req) {
        Map<String, List<?>> featureObjectMap = new ConcurrentHashMap<>();
        NUser nUser = repoService.findNUserById(user.getId());
        Set<Prefer> prefers = new HashSet<>();
        if (req.getAmbiances() != null && !req.getAmbiances().isEmpty()) {
            List<Ambiance> ambiances = cacheableService.findAmbiances().stream()
                    .filter(a -> req.getAmbiances().contains(a.getId()))
                    .toList();
            for (final Ambiance ambiance : ambiances) {
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance.getId());
                prefers.add(Prefer.builder().feature(nAmbiance).build());
            }
            featureObjectMap.put("ambiances", ambiances);
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (final Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity.getId());
                prefers.add(Prefer.builder().feature(nAmenity).build());
            }
            featureObjectMap.put("amenities", amenities);
        }

        if (req.getPurposes() != null && !req.getPurposes().isEmpty()) {
            List<Purpose> purposes = cacheableService.findPurposes().stream()
                    .filter(a -> req.getPurposes().contains(a.getId()))
                    .toList();
            for (final Purpose purpose : purposes) {
                NPurpose nPurpose = repoService.findNPurposeById(purpose.getId());
                prefers.add(Prefer.builder().feature(nPurpose).build());
            }
            featureObjectMap.put("purposes", purposes);
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (final Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity.getId());
                prefers.add(Prefer.builder().feature(nCapacity).build());
            }
            featureObjectMap.put("capacities", capacities);
        }

        if (req.getCategories() != null && !req.getCategories().isEmpty()) {
            List<Category> categories = cacheableService.findCategories().stream()
                    .filter(c -> req.getCategories().contains(c.getId()))
                    .toList();
            for (final Category category : categories) {
                NCategory nCategory = repoService.findNCategoryById(category.getId());
                prefers.add(Prefer.builder().feature(nCategory).build());
            }
            featureObjectMap.put("categories", categories);
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (final DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode.getId());
                prefers.add(Prefer.builder().feature(nDressCode).build());
            }
            featureObjectMap.put("dressCodes", dressCodes);
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (final Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment.getId());
                prefers.add(Prefer.builder().feature(nEntertainment).build());
            }
            featureObjectMap.put("entertainments", entertainments);
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (final Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking.getId());
                prefers.add(Prefer.builder().feature(nParking).build());
            }
            featureObjectMap.put("parkings", parkings);
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (final Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price.getId());
                prefers.add(Prefer.builder().feature(nPrice).build());
            }
            featureObjectMap.put("prices", prices);
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (final ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType.getId());
                prefers.add(Prefer.builder().feature(nServiceType).build());
            }
            featureObjectMap.put("serviceTypes", serviceTypes);
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (final Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space.getId());
                prefers.add(Prefer.builder().feature(nSpace).build());
            }
            featureObjectMap.put("spaces", spaces);
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (final Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty.getId());
                prefers.add(Prefer.builder().feature(nSpecialty).build());
            }
            featureObjectMap.put("specialties", specialties);
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (final VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime.getId());
                prefers.add(Prefer.builder().feature(nVisitTime).build());
            }
            featureObjectMap.put("visitTimes", visitTimes);
        }

        nUser.setPreferCoffeeShops(prefers);
        repoService.saveNUser(nUser);
        featureObjectMap.forEach((key, value) -> {
            if(value == null || value.isEmpty())
                featureObjectMap.remove(key);
        });
        String preferenceString = null;
        try {
            preferenceString = objectMapper.writeValueAsString(featureObjectMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        user.setCoffeePreference(preferenceString);
        userRepository.save(user);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> followUser(User user,
                                                       Long followingWhoId) {
        // Check if users exist
        Optional<User> optionalFollowee = userRepository.findById(followingWhoId);
        if (optionalFollowee.isEmpty())
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        // Check if already following
        UserFollow.UserFollowerId id = new UserFollow.UserFollowerId(followingWhoId,
                                                                     user.getId());
        if (userFollowRepository.existsById(id)) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.ALREADY_FOLLOWING)));
        }

        // Create and save follow relationship
        UserFollow userFollow = new UserFollow();
        userFollow.setUserFollowerId(id);
        userFollowRepository.save(userFollow);
        NUser u1 = repoService.findNUserById(user.getId());
        NUser u2 = repoService.findNUserById(followingWhoId);
        u1.addFollowing(u2);
        repoService.saveNUser(u1);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> unfollowUser(User user,
                                                         Long unfollowingWhoId) {
        UserFollow.UserFollowerId id = new UserFollow.UserFollowerId(unfollowingWhoId,
                                                                     user.getId());

        if (!userFollowRepository.existsById(id)) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOLLOWING)));
        }

        userFollowRepository.deleteById(id);
        NUser u1 = repoService.findNUserById(user.getId());
        NUser u2 = repoService.findNUserById(unfollowingWhoId);
        u1.removeFollowing(u2);
        repoService.saveNUser(u1);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public Page<FollowerDTO> getFollowers(Long userId,
                                          Pageable pageable) {
        // Get users who follow the specified user
        Page<User> followers = userFollowRepository.findFollowersByFolloweeId(userId,
                                                                              pageable);
        final Set<Long> relation = userFollowRepository.findRelationByIdIn(
                followers
                        .getContent()
                        .stream()
                        .map(u -> new UserFollow.UserFollowerId(u.getId(),
                                                                userId))
                        .toList());


        return followers.map(u -> new FollowerDTO(u,
                                                  relation.contains(u.getId())));
    }

    public Page<FollowingDTO> getFollowing(Long userId,
                                           Pageable pageable) {
        // Get users who the specified user follows
        Page<User> following = userFollowRepository.findFollowingsByFollowerId(userId,
                                                                               pageable);
        return following.map(User::toFollowingDTO);
    }

    public Page<UserDTO> search(UserSearchRequest request,
                                Pageable pageable) {
        String name = request.getName();
        Page<User> users = userRepository.findByUsernameOrNameOrDisplayNameContainingIgnoreCase(name,
                                                                                                name,
                                                                                                name,
                                                                                                pageable);
        return users.map(User::toUserDTO);
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> updateUserProfile(User user,
                                                              UserUpdateDTO userUpdateDTO) {
        // Update only the fields that are provided in the DTO
        if (userUpdateDTO.getDisplayName() != null) {
            user.setDisplayName(userUpdateDTO.getDisplayName());
            NUser u1 = repoService.findNUserById(user.getId());
            u1.setDisplayName(userUpdateDTO.getDisplayName());
            repoService.saveNUser(u1);
        }

//        if (userUpdateDTO.getBio() != null) {
//            user.setBio(userUpdateDTO.getBio());
//        }

//        if (userUpdateDTO.getCoffeePreference() != null) {
//            user.setCoffeePreference(userUpdateDTO.getCoffeePreference());
//        }

        if (StringUtils.isNotBlank(userUpdateDTO.getPhone())) {
            user.setPhone(userUpdateDTO.getPhone());
        }

        if (StringUtils.isNotBlank(userUpdateDTO.getDob())) {
            user.setDob(DateTimeUtil.convertYYYYMMDDStrToLocalDate(userUpdateDTO.getDob()));
        }

        if (StringUtils.isNotBlank(userUpdateDTO.getGender())) {
            user.setGender(userUpdateDTO.getGender());
        }

        // For nested objects like address, you might want to handle them differently
//        if (userUpdateDTO.getAddress() != null) {
//            // Update address or create a new one if it doesn't exist
//            if (user.getAddress() == null) {
//                Address address = new Address();
//                BeanUtils.copyProperties(userUpdateDTO.getAddress(),
//                                         address);
//                addressRepository.save(address);
//                user.setAddress(address);
//            } else {
//                BeanUtils.copyProperties(userUpdateDTO.getAddress(),
//                                         user.getAddress());
//            }
//        }

        // Save the updated user
        user = userRepository.save(user);

        // Convert to DTO and return
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             new UserProfile(user)));
    }

    public ResponseEntity<ResponseMetaData> getTopContributors(int limit) {
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
                contributor.setUser(user.toUserDTO());
                contributor.setContributionCount(contributionCount);
                contributors.add(contributor);
            }
        }

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             contributors));
    }

    public Page<Image> getPhotos(Long id,
                                 Pageable pageRequest) {
        return reviewRepository.findPhotosByUserId(id,
                                                   pageRequest);
    }

    public ResponseEntity<ResponseMetaData> getRecentPhotos(Long userId) {
        final List<Image> photosByUserId = reviewRepository.findPhotosByUserId(userId);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             photosByUserId));
    }

    public ResponseEntity<ResponseMetaData> getRecentFollowing(Long id) {
        List<User> recentFollowing = userFollowRepository.findFollowingsByFollowerId(id);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             recentFollowing.stream().map(User::toUserDTO)));
    }

    public ResponseEntity<ResponseMetaData> getRecentFollowers(Long id) {
        final List<User> recentFollowers = userFollowRepository.findFollowersByFolloweeId(id);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             recentFollowers.stream().map(User::toUserDTO)));
    }

    public ResponseEntity<ResponseMetaData> migrateUsers() {
        final List<User> all = repoService.fetchUsersFromPostgres();
        List<NUser> nUsers = new ArrayList<>();
        for (final User user : all) {
            NUser nUser = NUser.builder()
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .profilePhoto(user.getProfilePhoto())
                    .build();
            nUsers.add(nUser);
        }
        repoService.saveUsersToNeo4j(nUsers);
        return ResponseEntity.ok().build();
    }

}
