package com.example.socialcoffee.service;

import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.neo4j.NCoffeeShop;
import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.neo4j.feature.*;
import com.example.socialcoffee.domain.neo4j.relationship.HasFeature;
import com.example.socialcoffee.domain.postgres.*;
import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.ContributionRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.*;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.model.CoffeeShopFilter;
import com.example.socialcoffee.repository.neo4j.NCoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopMoodRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeShopService {
    private final NCoffeeShopRepository nCoffeeShopRepository;

    private final CoffeeShopRepository coffeeShopRepository;

    private final AddressRepository addressRepository;

    private final CacheableService cacheableService;

    private final CloudinaryService cloudinaryService;

    private final ImageService imageService;

    private final RepoService repoService;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final CoffeeShopMoodRepository coffeeShopMoodRepository;

    private final Neo4jClient neo4jClient;

    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(final Double lat, final Double lng, final Pageable pageable) {
        final List<CoffeeShop> coffeeShops = coffeeShopRepository.findAll(pageable).getContent();
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                lat,
                lng)).toList();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                coffeeShopVMs));
    }

    public ResponseEntity<ResponseMetaData> getCoffeeShopById(Long id, User user, CoffeeShopSearchRequest filter) {
        try {
            Optional<CoffeeShop> coffeeShopOptional = coffeeShopRepository.findById(id);
            if (coffeeShopOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST)));
            }
            CoffeeShop coffeeShop = coffeeShopOptional.get();
            coffeeShop.updateGalleryPhotos(Collections.singletonList(Image.builder().url(coffeeShop.getCoverPhoto()).build()));
            CoffeeShopDetailVM coffeeShopDetailVM = new CoffeeShopDetailVM(coffeeShop);
            if (Objects.nonNull(user) && !SecurityUtil.getUserRole().contains(RoleEnum.ADMIN.getValue())) {
                if (user.getCoffeePreference() != null) {
                    SearchFilter preference = objectMapper.readValue(
                            user.getCoffeePreference(),
                            new TypeReference<>() {
                            }
                    );
                    if (Objects.nonNull(preference))
                        coffeeShopDetailVM.setFeatureDto(preference);
                }
                if (Objects.nonNull(filter))
                    coffeeShopDetailVM.setFeatureDto(filter);
            }
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                    coffeeShopDetailVM));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public ResponseEntity<ResponseMetaData> getCoffeeShopByIdInEdit(Long id) {
        try {
            Optional<CoffeeShop> coffeeShopOptional = coffeeShopRepository.findById(id);
            if (coffeeShopOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST)));
            }
            CoffeeShopEditVM coffeeShopEditVM = new CoffeeShopEditVM();
            CoffeeShop coffeeShop = coffeeShopOptional.get();
            BeanUtils.copyProperties(coffeeShop,
                    coffeeShopEditVM);
            coffeeShopEditVM.setOpenTime(coffeeShop.getOpenHour());
            coffeeShopEditVM.setCloseTime(coffeeShop.getCloseHour());
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                    coffeeShopEditVM));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public PageDtoOut<CoffeeShopVM> search(CoffeeShopSearchRequest request, Integer page, Integer size, Sort sort) {
        try {
            Page<CoffeeShop> coffeeShops = coffeeShopRepository.searchCoffeeShops(request, page, size, sort);
            List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                            request.getLatitude(),
                            request.getLongitude()))
                    .collect(Collectors.toList());
            return PageDtoOut.from(page, size, coffeeShops.getTotalElements(), coffeeShopVMs);
        } catch (EmptyResultDataAccessException e) {
            return PageDtoOut.from(page, size, 0, Collections.emptyList());
        }
    }

    public ResponseEntity<ResponseMetaData> getSponsoredCoffeeShop(Double latitude,
                                                                   Double longitude) {
        List<CoffeeShop> coffeeShops = coffeeShopRepository.findByIsSponsored(true);
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                latitude,
                longitude)).toList();
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                coffeeShopVMs));
    }

    @Cacheable(value = "search_filter", key = "'SEARCH_FILTERS'")
    public SearchFilter getSearchFilters() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setAmbiances(cacheableService.findAmbiances());
        searchFilter.setAmenities(cacheableService.findAmenities());
        searchFilter.setCapacities(cacheableService.findCapacities());
        searchFilter.setEntertainments(cacheableService.findEntertainments());
        searchFilter.setParkings(cacheableService.findParkings());
        searchFilter.setPrices(cacheableService.findPrices());
        searchFilter.setPurposes(cacheableService.findPurposes());
        searchFilter.setServiceTypes(cacheableService.findServiceTypes());
        searchFilter.setSpaces(cacheableService.findSpaces());
        searchFilter.setSpecialties(cacheableService.findSpecialties());
        searchFilter.setVisitTimes(cacheableService.findVisitTimes());
        searchFilter.setDistances(Arrays.stream(Distance.values()).map(d -> new SearchFilter.DistanceDTO((long) d.ordinal(),
                d.getValue())).collect(Collectors.toList()));
        searchFilter.setSorts(Arrays.stream(CoffeeShopSort.values()).map(s -> new SearchFilter.SortDTO((long) s.ordinal(),
                s.getValue())).collect(Collectors.toList()));
        return searchFilter;
    }

    public CoffeeShopFilter getCoffeeShopFilters() {
        CoffeeShopFilter filter = new CoffeeShopFilter();
        filter.setDistances(Arrays.stream(Distance.values()).map(Enum::name).toList());
        filter.setAmbiances(cacheableService.findAmbiances().stream().map(Feature::getValue).toList());
        filter.setAmenities(cacheableService.findAmenities().stream().map(Feature::getValue).toList());
        filter.setCapacities(cacheableService.findCapacities().stream().map(Feature::getValue).toList());
        filter.setParkings(cacheableService.findParkings().stream().map(Feature::getValue).toList());
        filter.setPrices(cacheableService.findPrices().stream().map(Feature::getValue).toList());
        filter.setPurposes(cacheableService.findPurposes().stream().map(Feature::getValue).toList());
        filter.setServiceTypes(cacheableService.findServiceTypes().stream().map(Feature::getValue).toList());
        filter.setCategories(cacheableService.findCategories().stream().map(Feature::getValue).toList());
        filter.setEntertainments(cacheableService.findEntertainments().stream().map(Feature::getValue).toList());
        filter.setSpaces(cacheableService.findSpaces().stream().map(Feature::getValue).toList());
        filter.setSpecialties(cacheableService.findSpecialties().stream().map(Feature::getValue).toList());
        filter.setVisitTimes(cacheableService.findVisitTimes().stream().map(Feature::getValue).toList());
        return filter;
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> updateCoffeeShopStatus(Long shopId,
                                                                   String newStatus) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }

        if (!Status.PENDING.getValue().equals(coffeeShop.getStatus())) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST)));
        }
        coffeeShop.setStatus(newStatus);
        coffeeShopRepository.save(coffeeShop);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public Page<CoffeeShopDTO> findCoffeeShops(String name,
                                               String status,
                                               Pageable pageable) {
        String statusValue = StringUtils.isBlank(status) ? status : null;

        Page<CoffeeShop> coffeeShops;
        if (name != null && statusValue != null) {
            coffeeShops = coffeeShopRepository.findByNameContainingIgnoreCaseAndStatus(name,
                    statusValue,
                    pageable);
        } else if (name != null) {
            coffeeShops = coffeeShopRepository.findByNameContainingIgnoreCase(name,
                    pageable);
        } else if (statusValue != null) {
            coffeeShops = coffeeShopRepository.findByStatus(statusValue,
                    pageable);
        } else {
            coffeeShops = coffeeShopRepository.findAll(pageable);
        }

        return coffeeShops.map(CoffeeShop::toCoffeeShopDTO);
    }

    public ResponseEntity<ResponseMetaData> migrateRelationship() {
        final List<CoffeeShop> all = repoService.fetchCoffeeShopsFromPostgres();
        for (final CoffeeShop coffeeShop : all) {
            repoService.migrateSingleCoffeeShop(coffeeShop);
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ResponseMetaData> likeCoffeeShop(Long shopId,
                                                           User currentUser) {
        NUser nUser = repoService.findNUserById(currentUser.getId());
        nUser.addLike(repoService.findNCoffeeShopById(shopId));
        repoService.saveNUser(nUser);
        final CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        currentUser.addLike(coffeeShop);
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ResponseMetaData> unlikeCoffeeShop(Long shopId,
                                                             User currentUser) {
        NUser nUser = repoService.findNUserById(currentUser.getId());
        nUser.removeLike(neo4jClient, currentUser.getId(), shopId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> createCoffeeShop(CreateCoffeeShopRequest req) {
        log.info("Starting creation of coffee shop: {}", req.getName());

        // Save address
        Address savedAddress = addressRepository.save(buildAddress(req));

        // Build coffee shop
        CoffeeShop coffeeShop = CoffeeShop.builder()
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .webAddress(req.getWebAddress())
                .menuWebAddress(req.getMenuWebAddress())
                .additionInfo(req.getAdditionInfo())
                .openHour(req.getOpenHour())
                .closeHour(req.getCloseHour())
                .address(savedAddress)
                .galleryPhotos(imageService.save(req.getGalleryPhotos()))
                .coverPhoto(cloudinaryService.upload(req.getCoverPhoto()))
                .description(req.getDescription())
                .createdBy(CommonConstant.ADMIN_INDEX)
                .status(Status.ACTIVE.getValue())
                .build();

        // Process features
        Set<HasFeature> hasFeatures = new HashSet<>();
        processFeatures(req.getAmbiances(), cacheableService::findAmbiances, repoService::findNAmbianceById, coffeeShop::setAmbiances, hasFeatures);
        processFeatures(req.getAmenities(), cacheableService::findAmenities, repoService::findNAmenityById, coffeeShop::setAmenities, hasFeatures);
        processFeatures(req.getCapacities(), cacheableService::findCapacities, repoService::findNCapacityById, coffeeShop::setCapacities, hasFeatures);
        processFeatures(req.getPurposes(), cacheableService::findPurposes, repoService::findNPurposeById, coffeeShop::setPurposes, hasFeatures);
        processFeatures(req.getDressCodes(), cacheableService::findDressCodes, repoService::findNDressCodeById, coffeeShop::setDressCodes, hasFeatures);
        processFeatures(req.getEntertainments(), cacheableService::findEntertainments, repoService::findNEntertainmentById, coffeeShop::setEntertainments, hasFeatures);
        processFeatures(req.getParkings(), cacheableService::findParkings, repoService::findNParkingById, coffeeShop::setParkings, hasFeatures);
        processFeatures(req.getPrices(), cacheableService::findPrices, repoService::findNPriceById, coffeeShop::setPrices, hasFeatures);
        processFeatures(req.getServiceTypes(), cacheableService::findServiceTypes, repoService::findNServiceTypeById, coffeeShop::setServiceTypes, hasFeatures);
        processFeatures(req.getSpaces(), cacheableService::findSpaces, repoService::findNSpaceById, coffeeShop::setSpaces, hasFeatures);
        processFeatures(req.getSpecialties(), cacheableService::findSpecialties, repoService::findNSpecialtyById, coffeeShop::setSpecialties, hasFeatures);
        processFeatures(req.getVisitTimes(), cacheableService::findVisitTimes, repoService::findNVisitTimeById, coffeeShop::setVisitTimes, hasFeatures);


        // Save coffee shop
        CoffeeShop saved = coffeeShopRepository.save(coffeeShop);

        // Build and save NCoffeeShop
        NCoffeeShop nCoffeeShop = NCoffeeShop.builder()
                .id(saved.getId())
                .name(saved.getName())
                .coverPhoto(saved.getCoverPhoto())
                .hasFeatures(hasFeatures)
                .build();
        repoService.saveNCoffeeShop(nCoffeeShop);

        // Async notification
        CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenFinishCreatingShop(
                saved.getId().toString(),
                saved.getName(),
                saved.getCoverPhoto()));

        log.info("Coffee shop created successfully: name = {}, id = {}", saved.getName(), saved.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), coffeeShop));
    }

    private <T extends Feature, N extends NFeature> void processFeatures(
            List<Long> featureIds,
            Supplier<List<T>> findFeatures,
            Function<T, N> mapToNFeature,
            Consumer<List<T>> setFeatures,
            Set<HasFeature> hasFeatures) {
        if (!CollectionUtils.isEmpty(featureIds)) {
            List<T> filteredFeatures = findFeatures.get().stream()
                    .filter(feature -> featureIds.contains(feature.getId()))
                    .collect(Collectors.toList());
            setFeatures.accept(filteredFeatures);
            filteredFeatures.stream()
                    .map(mapToNFeature)
                    .map(nFeature -> HasFeature.builder().feature(nFeature).build())
                    .forEach(hasFeatures::add);
        }
    }

    private Address buildAddress(CreateCoffeeShopRequest req) {
        return Address.builder()
                .googleMapUrl(req.getGoogleMapUrl())
                .addressDetail(req.getAddressDetail())
                .province(req.getProvince())
                .district(req.getDistrict())
                .ward(req.getWard())
                .longitude(req.getLongitude())
                .latitude(req.getLatitude())
                .location(GeometryUtil.parseLocation(req.getLongitude(), req.getLatitude()))
                .build();
    }

    @Transactional
    public void createCoffeeShop(ContributionRequest req) {
        Address address = buildAddress(req);
        log.info("Start create coffee shop with name = {}",
                req.getName());
        Address savedAddress = addressRepository.save(address);

        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.setName(req.getName());
        coffeeShop.setPhoneNumber(req.getPhoneNumber());
        coffeeShop.setWebAddress(req.getWebAddress());
        coffeeShop.setMenuWebAddress(req.getMenuWebAddress());
        coffeeShop.setAdditionInfo(req.getAdditionInfo());
        coffeeShop.setOpenHour(req.getOpenHour());
        coffeeShop.setCloseHour(req.getCloseHour());
        coffeeShop.setAddress(savedAddress);

        Set<HasFeature> hasFeatures = new HashSet<>();
        processFeatures(req.getAmbiances(), cacheableService::findAmbiances, repoService::findNAmbianceById, coffeeShop::setAmbiances, hasFeatures);
        processFeatures(req.getAmenities(), cacheableService::findAmenities, repoService::findNAmenityById, coffeeShop::setAmenities, hasFeatures);
        processFeatures(req.getCapacities(), cacheableService::findCapacities, repoService::findNCapacityById, coffeeShop::setCapacities, hasFeatures);
        processFeatures(req.getPurposes(), cacheableService::findPurposes, repoService::findNPurposeById, coffeeShop::setPurposes, hasFeatures);
        processFeatures(req.getDressCodes(), cacheableService::findDressCodes, repoService::findNDressCodeById, coffeeShop::setDressCodes, hasFeatures);
        processFeatures(req.getEntertainments(), cacheableService::findEntertainments, repoService::findNEntertainmentById, coffeeShop::setEntertainments, hasFeatures);
        processFeatures(req.getParkings(), cacheableService::findParkings, repoService::findNParkingById, coffeeShop::setParkings, hasFeatures);
        processFeatures(req.getPrices(), cacheableService::findPrices, repoService::findNPriceById, coffeeShop::setPrices, hasFeatures);
        processFeatures(req.getServiceTypes(), cacheableService::findServiceTypes, repoService::findNServiceTypeById, coffeeShop::setServiceTypes, hasFeatures);
        processFeatures(req.getSpaces(), cacheableService::findSpaces, repoService::findNSpaceById, coffeeShop::setSpaces, hasFeatures);
        processFeatures(req.getSpecialties(), cacheableService::findSpecialties, repoService::findNSpecialtyById, coffeeShop::setSpecialties, hasFeatures);
        processFeatures(req.getVisitTimes(), cacheableService::findVisitTimes, repoService::findNVisitTimeById, coffeeShop::setVisitTimes, hasFeatures);

        coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotoPaths()));
        coffeeShop.setCoverPhoto(req.getCoverPhotoPath());
        coffeeShop.setCreatedBy(CommonConstant.ADMIN_INDEX);
        coffeeShop.setStatus(Status.ACTIVE.getValue());
        CoffeeShop saved = coffeeShopRepository.save(coffeeShop);
        NCoffeeShop nCoffeeShop = NCoffeeShop.builder()
                .id(saved.getId())
                .name(saved.getName())
                .coverPhoto(saved.getCoverPhoto())
                .hasFeatures(hasFeatures)
                .build();
        repoService.saveNCoffeeShop(nCoffeeShop);
        log.info("Finish create coffee shop with name = {}, id = {}", req.getName(), coffeeShop.getId());
    }

    private Address buildAddress(ContributionRequest req) {
        return Address.builder()
                .googleMapUrl(req.getGoogleMapUrl())
                .addressDetail(req.getAddressDetail())
                .province(req.getProvince())
                .district(req.getDistrict())
                .ward(req.getWard())
                .longitude(req.getLongitude())
                .latitude(req.getLatitude())
                .location(GeometryUtil.parseLocation(req.getLongitude(),
                        req.getLatitude()))
                .build();
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> updateCoffeeShop(CreateCoffeeShopRequest req,
                                                             Long id) {
        log.info("Start update coffee shop with id = {}, name = {}",
                id,
                req.getName());

        // Verify coffee shop exists
        CoffeeShop coffeeShop = coffeeShopRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coffee shop not found with id: " + id));

        // Update address
        Address address = coffeeShop.getAddress();
        updateAddress(address,
                req);
        Address savedAddress = addressRepository.save(address);

        // Update coffee shop basic info
        coffeeShop.setName(req.getName());
        coffeeShop.setPhoneNumber(req.getPhoneNumber());
        coffeeShop.setWebAddress(req.getWebAddress());
        coffeeShop.setMenuWebAddress(req.getMenuWebAddress());
        coffeeShop.setAdditionInfo(req.getAdditionInfo());
        coffeeShop.setOpenHour(req.getOpenHour());
        coffeeShop.setCloseHour(req.getCloseHour());
        coffeeShop.setAddress(savedAddress);
        coffeeShop.setDescription(req.getDescription());

        // Update photos if provided
        if (req.getGalleryPhotos() != null && req.getGalleryPhotos().length > 0) {
            coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotos()));
        }
        if (req.getCoverPhoto() != null) {
            coffeeShop.setCoverPhoto(cloudinaryService.upload(req.getCoverPhoto()));
        }

        // Update features
        Set<HasFeature> hasFeatures = new HashSet<>();
        processFeatures(req.getAmbiances(), cacheableService::findAmbiances, repoService::findNAmbianceById, coffeeShop::setAmbiances, hasFeatures);
        processFeatures(req.getAmenities(), cacheableService::findAmenities, repoService::findNAmenityById, coffeeShop::setAmenities, hasFeatures);
        processFeatures(req.getCapacities(), cacheableService::findCapacities, repoService::findNCapacityById, coffeeShop::setCapacities, hasFeatures);
        processFeatures(req.getPurposes(), cacheableService::findPurposes, repoService::findNPurposeById, coffeeShop::setPurposes, hasFeatures);
        processFeatures(req.getDressCodes(), cacheableService::findDressCodes, repoService::findNDressCodeById, coffeeShop::setDressCodes, hasFeatures);
        processFeatures(req.getEntertainments(), cacheableService::findEntertainments, repoService::findNEntertainmentById, coffeeShop::setEntertainments, hasFeatures);
        processFeatures(req.getParkings(), cacheableService::findParkings, repoService::findNParkingById, coffeeShop::setParkings, hasFeatures);
        processFeatures(req.getPrices(), cacheableService::findPrices, repoService::findNPriceById, coffeeShop::setPrices, hasFeatures);
        processFeatures(req.getServiceTypes(), cacheableService::findServiceTypes, repoService::findNServiceTypeById, coffeeShop::setServiceTypes, hasFeatures);
        processFeatures(req.getSpaces(), cacheableService::findSpaces, repoService::findNSpaceById, coffeeShop::setSpaces, hasFeatures);
        processFeatures(req.getSpecialties(), cacheableService::findSpecialties, repoService::findNSpecialtyById, coffeeShop::setSpecialties, hasFeatures);
        processFeatures(req.getVisitTimes(), cacheableService::findVisitTimes, repoService::findNVisitTimeById, coffeeShop::setVisitTimes, hasFeatures);

        // Save updated coffee shop
        CoffeeShop saved = coffeeShopRepository.save(coffeeShop);

        // Update NCoffeeShop
        NCoffeeShop nCoffeeShop = repoService.findNCoffeeShopById(saved.getId());
        nCoffeeShop.setName(saved.getName());
        nCoffeeShop.setCoverPhoto(saved.getCoverPhoto());
        nCoffeeShop.setHasFeatures(hasFeatures);
        nCoffeeShopRepository.clearAllFeatures(saved.getId());
        repoService.saveNCoffeeShop(nCoffeeShop);
        CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenFinishUpdatingShop(saved.getId().toString(),
                saved.getName(),
                saved.getCoverPhoto()));
        log.info("Finish update coffee shop with id = {}, name = {}",
                id,
                req.getName());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                coffeeShop));
    }

    // Helper method to update address
    private void updateAddress(Address address,
                               CreateCoffeeShopRequest req) {
        address.setGoogleMapUrl(req.getGoogleMapUrl());
        address.setAddressDetail(req.getAddressDetail());
        address.setProvince(req.getProvince());
        address.setDistrict(req.getDistrict());
        address.setWard(req.getWard());
        address.setLongitude(req.getLongitude());
        address.setLatitude(req.getLatitude());
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> deleteCoffeeShop(Long id) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(id);
        if (Objects.isNull(coffeeShop)) throw new NotFoundException();
        coffeeShop.setStatus(Status.INACTIVE.getValue());
        NCoffeeShop nCoffeeShop = repoService.findNCoffeeShopById(id);
        nCoffeeShop.setStatus(Status.INACTIVE.getValue());
        repoService.saveNCoffeeShop(nCoffeeShop);
        coffeeShopRepository.save(coffeeShop);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> sponsorCoffeeShop(Long id) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(id);
        if (Objects.isNull(coffeeShop)) throw new NotFoundException();
        List<Long> ids = coffeeShopRepository.findIdByIsSponsored(Boolean.TRUE);
        if (ids.size() >= CommonConstant.MAX_SPONSORED_SHOP && !ids.contains(id))
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.EXCEED_MAX_SPONSORED_SHOP,
                    CommonConstant.MAX_SPONSORED_SHOP.toString())));
        coffeeShop.setIsSponsored(!coffeeShop.getIsSponsored());
        coffeeShopRepository.save(coffeeShop);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public void toggleCoffeeShopMood(Long shopId,
                                     Long userId,
                                     String mood) {
        try {
            Mood.valueOf(mood.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mood: " + mood);
        }

        CoffeeShopMood existingMood = coffeeShopMoodRepository
                .findByShopIdAndUserIdAndMood(shopId,
                        userId,
                        mood);

        if (existingMood != null) {
            coffeeShopMoodRepository.delete(existingMood);
        } else {
            CoffeeShopMood newMood = CoffeeShopMood.builder()
                    .shopId(shopId)
                    .userId(userId)
                    .mood(mood)
                    .build();
            coffeeShopMoodRepository.save(newMood);
        }
    }

    @Transactional
    public void populateCoffeeShopMoods() {
        Random random = new Random();
        List<Long> userIds = cacheableService.getActiveUsers().stream()
                .map(User::getId)
                .toList();
        List<Long> shopIds = coffeeShopRepository.findAll().stream()
                .map(CoffeeShop::getId)
                .toList();
        Mood[] moods = Mood.values();

        for (Long userId : userIds) {
            for (Long shopId : shopIds) {
                coffeeShopMoodRepository.deleteByShopIdAndUserId(shopId,
                        userId);

                int numMoods = random.nextInt(2) + 1; // 1 or 2
                for (int i = 0; i < numMoods; i++) {
                    String mood = moods[random.nextInt(moods.length)].getValue();
                    // Avoid duplicate moods for the same shop and user
                    if (coffeeShopMoodRepository.findByShopIdAndUserIdAndMood(shopId,
                            userId,
                            mood) == null) {
                        log.info("Save coffee shop id = {}, user id = {}, mood = {}",
                                shopId,
                                userId,
                                mood);
                        CoffeeShopMood newMood = CoffeeShopMood.builder()
                                .shopId(shopId)
                                .userId(userId)
                                .mood(mood)
                                .build();
                        coffeeShopMoodRepository.save(newMood);
                        log.info("Finish coffee shop id = {}, user id = {}, mood = {}",
                                shopId,
                                userId,
                                mood);
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public MoodCountDto getCoffeeShopMoodCounts(Long shopId) {
        MoodCountDto moodCountDto = new MoodCountDto();
        List<CoffeeShopMood> moods = coffeeShopMoodRepository.findByShopId(shopId);
        Map<String, Long> moodCounts = new HashMap<>();
        for (Mood mood : Mood.values()) {
            moodCounts.put(mood.getValue(),
                    0L);
        }
        for (CoffeeShopMood mood : moods) {
            moodCounts.merge(mood.getMood(),
                    1L,
                    Long::sum);
        }
        moodCountDto.setMoodCounts(moodCounts);
        List<String> userMoodCounts = new ArrayList<>();
        final List<CoffeeShopMood> userMoodCheckedList = moods.stream().filter(m -> m.getUserId().equals(SecurityUtil.getUserId())).toList();
        for (final CoffeeShopMood coffeeShopMood : userMoodCheckedList) {
            userMoodCounts.add(coffeeShopMood.getMood().toLowerCase());
        }
        moodCountDto.setUserMoodCounts(userMoodCounts);
        return moodCountDto;
    }

    public PageDtoOut<CoffeeShopVM> searchByMood(Mood mood,
                                                 Double latitude,
                                                 Double longitude,
                                                 PageRequest pageRequest) {
        Page<Long> topCoffeeShopByMood = coffeeShopRepository.findTopCoffeeShopByMood(mood.getValue(),
                pageRequest);
        List<CoffeeShop> coffeeShops = coffeeShopRepository.findAllById(topCoffeeShopByMood.getContent());
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                latitude,
                longitude)).collect(Collectors.toList());
        PageDtoOut<CoffeeShopVM> res = PageDtoOut.from(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                topCoffeeShopByMood.getTotalElements(),
                coffeeShopVMs);
        res.setMetaData(mood.getMessage());
        return res;
    }
}
