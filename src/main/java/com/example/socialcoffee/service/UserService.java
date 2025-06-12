package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.neo4j.feature.*;
import com.example.socialcoffee.domain.neo4j.relationship.Prefer;
import com.example.socialcoffee.domain.postgres.*;
import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.*;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.UserSettingModel;
import com.example.socialcoffee.repository.neo4j.NUserRepository;
import com.example.socialcoffee.repository.postgres.ReviewRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.repository.postgres.UserSettingRepository;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.ObjectUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final NUserRepository nUserRepository;

//    private final UserFollowRepository userFollowRepository;

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    private final CacheableService cacheableService;

    private final ObjectMapper objectMapper;

    private final RepoService repoService;

    private final Neo4jClient neo4jClient;

    private final UserSettingRepository userSettingRepository;

    public ResponseEntity<ResponseMetaData> getMyProfile(User user) {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                new UserProfile(user)));
    }

    public ResponseEntity<ResponseMetaData> getUserProfile(User user, String displayName) {
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
            isFollowing = nUserRepository.userFollowExist(viewingUserId, currentUserId);
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                new UserProfile(user,
                        isFollowing)));
    }

    public ResponseEntity<ResponseMetaData> getUserCoffeeShopPreference(User user) {
        try {
            SearchFilter preference = objectMapper.readValue(
                    user.getCoffeePreference(),
                    new TypeReference<>() {
                    }
            );
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                    preference));
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
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance);
                prefers.add(Prefer.builder().feature(nAmbiance).build());
            }
            featureObjectMap.put("ambiances",
                    ambiances);
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (final Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity);
                prefers.add(Prefer.builder().feature(nAmenity).build());
            }
            featureObjectMap.put("amenities",
                    amenities);
        }

        if (req.getPurposes() != null && !req.getPurposes().isEmpty()) {
            List<Purpose> purposes = cacheableService.findPurposes().stream()
                    .filter(a -> req.getPurposes().contains(a.getId()))
                    .toList();
            for (final Purpose purpose : purposes) {
                NPurpose nPurpose = repoService.findNPurposeById(purpose);
                prefers.add(Prefer.builder().feature(nPurpose).build());
            }
            featureObjectMap.put("purposes",
                    purposes);
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (final Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity);
                prefers.add(Prefer.builder().feature(nCapacity).build());
            }
            featureObjectMap.put("capacities",
                    capacities);
        }

        if (req.getCategories() != null && !req.getCategories().isEmpty()) {
            List<Category> categories = cacheableService.findCategories().stream()
                    .filter(c -> req.getCategories().contains(c.getId()))
                    .toList();
            for (final Category category : categories) {
                NCategory nCategory = repoService.findNCategoryById(category);
                prefers.add(Prefer.builder().feature(nCategory).build());
            }
            featureObjectMap.put("categories",
                    categories);
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (final DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode);
                prefers.add(Prefer.builder().feature(nDressCode).build());
            }
            featureObjectMap.put("dressCodes",
                    dressCodes);
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (final Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment);
                prefers.add(Prefer.builder().feature(nEntertainment).build());
            }
            featureObjectMap.put("entertainments",
                    entertainments);
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (final Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking);
                prefers.add(Prefer.builder().feature(nParking).build());
            }
            featureObjectMap.put("parkings",
                    parkings);
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (final Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price);
                prefers.add(Prefer.builder().feature(nPrice).build());
            }
            featureObjectMap.put("prices",
                    prices);
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (final ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType);
                prefers.add(Prefer.builder().feature(nServiceType).build());
            }
            featureObjectMap.put("serviceTypes",
                    serviceTypes);
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (final Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space);
                prefers.add(Prefer.builder().feature(nSpace).build());
            }
            featureObjectMap.put("spaces",
                    spaces);
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (final Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty);
                prefers.add(Prefer.builder().feature(nSpecialty).build());
            }
            featureObjectMap.put("specialties",
                    specialties);
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (final VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime);
                prefers.add(Prefer.builder().feature(nVisitTime).build());
            }
            featureObjectMap.put("visitTimes",
                    visitTimes);
        }
        nUserRepository.clearAllPreferences(user.getId());
        nUser.setPreferCoffeeShops(prefers);
        repoService.saveNUser(nUser);
        featureObjectMap.forEach((key, value) -> {
            if (value == null || value.isEmpty())
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
        cacheableService.clearRecommendation(user.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> followUser(User user,
                                                       Long followingWhoId) {
        NUser u1 = repoService.findNUserById(user.getId());
        u1.removeFollowing(neo4jClient, followingWhoId);
        u1.addFollowing(neo4jClient, followingWhoId);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> unfollowUser(User user,
                                                         Long unfollowingWhoId) {
        NUser u1 = repoService.findNUserById(user.getId());
        u1.removeFollowing(neo4jClient, unfollowingWhoId);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public Page<FollowerDTO> getFollowers(Long userId,
                                          Pageable pageable) {
        // Get users who follow the specified user
        Page<NUser> followers = nUserRepository.findFollowers(userId,
                pageable);

        return followers.map(u -> new FollowerDTO(u, true));
    }

    public Page<FollowingDTO> getFollowing(Long userId,
                                           Pageable pageable) {
        // Get users who the specified user follows
        Page<NUser> following = nUserRepository.findFollowings(userId, pageable);
        return following.map(u -> new FollowingDTO(u, true));
    }

    public Page<UserDTO> search(UserSearchRequest request,
                                Pageable pageable) {
        String name = request.getName();
        Page<User> users = userRepository.findByUsernameOrFullNameOrDisplayNameContainingIgnoreCase(name,
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

//    public ResponseEntity<ResponseMetaData> getRecentFollowing(Long id) {
//        List<User> recentFollowing = userFollowRepository.findFollowingsByFollowerId(id);
//        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
//                recentFollowing.stream().map(User::toUserDTO)));
//    }
//
//    public ResponseEntity<ResponseMetaData> getRecentFollowers(Long id) {
//        final List<User> recentFollowers = userFollowRepository.findFollowersByFolloweeId(id);
//        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
//                recentFollowers.stream().map(User::toUserDTO)));
//    }

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

    public ResponseEntity<ResponseMetaData> getContributions(User user,
                                                             String name,
                                                             String status,
                                                             final String type,
                                                             PageDtoIn pageDtoIn) {
        List<CoffeeShopContribution> contributions = user.getContributions().stream().filter(c -> c.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        if (StringUtils.isNotBlank(name))
            contributions = contributions.stream().filter(c -> c.getName().contentEquals(name)).collect(Collectors.toList());
        if (StringUtils.isNotBlank(status))
            contributions = contributions.stream().filter(c -> c.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
        List<ContributionVM> contributionVMS = contributions.stream().map(c -> {
            ContributionVM contributionVM = new ContributionVM();
            contributionVM.setCreatedAt(DateTimeUtil.convertLocalDateToString(c.getCreatedAt()));
            contributionVM.setUpdatedAt(DateTimeUtil.convertLocalDateToString(c.getUpdatedAt()));
            contributionVM.setStatus(c.getStatus());
            contributionVM.setComment(c.getReviewComments());
            try {
                contributionVM.setData(objectMapper.readValue(c.getContribution(),
                        ContributionRequest.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return contributionVM;
        }).toList();
        final List<ContributionVM> res = ObjectUtil.getPageResult(contributionVMS,
                pageDtoIn.getPage() - 1,
                pageDtoIn.getSize());
        PageDtoOut<ContributionVM> pageDtoOut = PageDtoOut.from(
                pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                contributions.size(),
                res
        );
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    public ResponseEntity<ResponseMetaData> getUserSetting() {
        Optional<UserSetting> optionalUserSetting = userSettingRepository.findById(SecurityUtil.getUserId());
        UserSettingModel userSettingModel;
        if (optionalUserSetting.isEmpty()) {
            UserSetting userSetting = new UserSetting();
            userSetting.setId(SecurityUtil.getUserId());
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

    public ResponseEntity<ResponseMetaData> updateUserSetting(UserSettingModel userSettingModel) {
        UserSetting userSetting = userSettingRepository.findById(SecurityUtil.getUserId()).get();
        String setting = ObjectUtil.objectToString(objectMapper, userSettingModel);
        userSetting.setSetting(setting);
        userSettingRepository.save(userSetting);
        return ResponseEntity.ok().body(new ResponseMetaData(
                new MetaDTO(MetaData.SUCCESS),
                userSettingModel
        ));
    }
}
