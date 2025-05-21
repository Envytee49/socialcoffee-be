package com.example.socialcoffee.service;

import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.ContributionRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.*;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.model.CoffeeShopFilter;
import com.example.socialcoffee.neo4j.NCoffeeShop;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.neo4j.feature.*;
import com.example.socialcoffee.neo4j.relationship.HasFeature;
import com.example.socialcoffee.repository.neo4j.NCoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopContributionRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeShopService {
    private final NCoffeeShopRepository nCoffeeShopRepository;
    private final CoffeeShopRepository coffeeShopRepository;
    private final GenerateTextService generateTextService;
    private final AddressRepository addressRepository;
    private final CacheableService cacheableService;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;
    private final RepoService repoService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CoffeeShopContributionRepository coffeeShopContributionRepository;
    private final NotificationService notificationService;

    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(final Double lat,
                                                             final Double lng,
                                                             final Pageable pageable) {
        final List<CoffeeShop> coffeeShops = coffeeShopRepository.findAll(pageable).getContent();
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                                                                                           lat,
                                                                                           lng)).toList();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      coffeeShopVMs));
    }

    public ResponseEntity<ResponseMetaData> getCoffeeShopById(Long id,
                                                              User user,
                                                              final CoffeeShopSearchRequest filter) {
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

    public PageDtoOut<CoffeeShopVM> search(CoffeeShopSearchRequest request,
                                           Integer page,
                                           Integer size,
                                           Sort sort) {
        Page<CoffeeShop> coffeeShops = coffeeShopRepository.searchCoffeeShops(request,
                                                                              page,
                                                                              size,sort);
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                                                                                           request.getLatitude(),
                                                                                           request.getLongitude()))
                .collect(Collectors.toList());
//        if (ObjectUtils.allNotNull(request.getDistance(),
//                                   request.getLongitude(),
//                                   request.getLatitude())) {
//            coffeeShopVMs.sort(Comparator.comparing(CoffeeShopVM::getDistance));
//        }
        return PageDtoOut.from(page,
                               size,
                               coffeeShops.getTotalElements(),
                               coffeeShopVMs);

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
        filter.setDistances(Arrays.stream(Distance.values()).map(d -> d.name()).toList());
        filter.setAmbiances(cacheableService.findAmbiances().stream().map(e -> e.getValue()).toList());
        filter.setAmenities(cacheableService.findAmenities().stream().map(e -> e.getValue()).toList());
        filter.setCapacities(cacheableService.findCapacities().stream().map(e -> e.getValue()).toList());
        filter.setParkings(cacheableService.findParkings().stream().map(e -> e.getValue()).toList());
        filter.setPrices(cacheableService.findPrices().stream().map(e -> e.getValue()).toList());
        filter.setPurposes(cacheableService.findPurposes().stream().map(e -> e.getValue()).toList());
        filter.setServiceTypes(cacheableService.findServiceTypes().stream().map(e -> e.getValue()).toList());
        filter.setCategories(cacheableService.findCategories().stream().map(e -> e.getValue()).toList());
        filter.setEntertainments(cacheableService.findEntertainments().stream().map(e -> e.getValue()).toList());
        filter.setSpaces(cacheableService.findSpaces().stream().map(e -> e.getValue()).toList());
        filter.setSpecialties(cacheableService.findSpecialties().stream().map(e -> e.getValue()).toList());
        filter.setVisitTimes(cacheableService.findVisitTimes().stream().map(e -> e.getValue()).toList());
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
        nUser.removeLike(repoService.findNCoffeeShopById(shopId));
        repoService.saveNUser(nUser);
        final CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        currentUser.removeLike(coffeeShop);
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
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
                .location(GeometryUtil.parseLocation(req.getLongitude(),
                                                     req.getLatitude()))
                .build();
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
    public ResponseEntity<ResponseMetaData> createCoffeeShop(User user,
                                                             CreateCoffeeShopRequest req) {
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
        if (req.getAmbiances() != null && !req.getAmbiances().isEmpty()) {
            List<Ambiance> ambiances = cacheableService.findAmbiances().stream()
                    .filter(a -> req.getAmbiances().contains(a.getId()))
                    .toList();
            for (final Ambiance ambiance : ambiances) {
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance);
                hasFeatures.add(HasFeature.builder().feature(nAmbiance).build());
            }
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (final Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity);
                hasFeatures.add(HasFeature.builder().feature(nAmenity).build());
            }
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (final Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity);
                hasFeatures.add(HasFeature.builder().feature(nCapacity).build());
            }
        }

        if (req.getPurposes() != null && !req.getPurposes().isEmpty()) {
            List<Purpose> purposes = cacheableService.findPurposes().stream()
                    .filter(c -> req.getPurposes().contains(c.getId()))
                    .toList();
            for (final Purpose purpose : purposes) {
                NPurpose nPurpose = repoService.findNPurposeById(purpose);
                hasFeatures.add(HasFeature.builder().feature(nPurpose).build());
            }
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (final DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode);
                hasFeatures.add(HasFeature.builder().feature(nDressCode).build());
            }
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (final Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment);
                hasFeatures.add(HasFeature.builder().feature(nEntertainment).build());
            }
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (final Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking);
                hasFeatures.add(HasFeature.builder().feature(nParking).build());
            }
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (final Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price);
                hasFeatures.add(HasFeature.builder().feature(nPrice).build());
            }
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (final ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType);
                hasFeatures.add(HasFeature.builder().feature(nServiceType).build());
            }
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (final Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space);
                hasFeatures.add(HasFeature.builder().feature(nSpace).build());
            }
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (final Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty);
                hasFeatures.add(HasFeature.builder().feature(nSpecialty).build());
            }
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (final VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime);
                hasFeatures.add(HasFeature.builder().feature(nVisitTime).build());
            }
        }

        coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotos()));
        coffeeShop.setCoverPhoto(cloudinaryService.upload(req.getCoverPhoto()));
        coffeeShop.setDescription(req.getDescription());
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
        CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenFinishCreatingShop(saved.getId().toString(),
                                                                               saved.getName(),
                                                                               saved.getCoverPhoto()));
        log.info("Finish create coffee shop with name = {}, id = {}",
                 req.getName(),
                 coffeeShop.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      coffeeShop));
    }



    @Transactional
    public void createCoffeeShop(User user,
                                 ContributionRequest req) {
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
        if (req.getAmbiances() != null && !req.getAmbiances().isEmpty()) {
            List<Ambiance> ambiances = cacheableService.findAmbiances().stream()
                    .filter(a -> req.getAmbiances().contains(a.getId()))
                    .toList();
            for (final Ambiance ambiance : ambiances) {
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance);
                hasFeatures.add(HasFeature.builder().feature(nAmbiance).build());
            }
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (final Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity);
                hasFeatures.add(HasFeature.builder().feature(nAmenity).build());
            }
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (final Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity);
                hasFeatures.add(HasFeature.builder().feature(nCapacity).build());
            }
        }

        if (req.getPurposes() != null && !req.getPurposes().isEmpty()) {
            List<Purpose> purposes = cacheableService.findPurposes().stream()
                    .filter(c -> req.getPurposes().contains(c.getId()))
                    .toList();
            for (final Purpose purpose : purposes) {
                NPurpose nPurpose = repoService.findNPurposeById(purpose);
                hasFeatures.add(HasFeature.builder().feature(nPurpose).build());
            }
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (final DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode);
                hasFeatures.add(HasFeature.builder().feature(nDressCode).build());
            }
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (final Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment);
                hasFeatures.add(HasFeature.builder().feature(nEntertainment).build());
            }
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (final Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking);
                hasFeatures.add(HasFeature.builder().feature(nParking).build());
            }
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (final Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price);
                hasFeatures.add(HasFeature.builder().feature(nPrice).build());
            }
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (final ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType);
                hasFeatures.add(HasFeature.builder().feature(nServiceType).build());
            }
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (final Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space);
                hasFeatures.add(HasFeature.builder().feature(nSpace).build());
            }
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (final Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty);
                hasFeatures.add(HasFeature.builder().feature(nSpecialty).build());
            }
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (final VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime);
                hasFeatures.add(HasFeature.builder().feature(nVisitTime).build());
            }
        }
        coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotoPaths()));
        coffeeShop.setCoverPhoto(req.getCoverPhotoPath());
//        coffeeShop.setDescription(req.getDescription());
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
        log.info("Finish create coffee shop with name = {}, id = {}",
                 req.getName(),
                 coffeeShop.getId());
    }

    public ResponseEntity<ResponseMetaData> updateCoffeeShop(User user,
                                                             CreateCoffeeShopRequest req,
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

        // Ambiances
        if (req.getAmbiances() != null) {
            List<Ambiance> ambiances = cacheableService.findAmbiances().stream()
                    .filter(a -> req.getAmbiances().contains(a.getId()))
                    .toList();
            for (Ambiance ambiance : ambiances) {
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance);
                hasFeatures.add(HasFeature.builder().feature(nAmbiance).build());
            }
        }

        // Amenities
        if (req.getAmenities() != null) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity);
                hasFeatures.add(HasFeature.builder().feature(nAmenity).build());
            }
        }

        // Capacities
        if (req.getCapacities() != null) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity);
                hasFeatures.add(HasFeature.builder().feature(nCapacity).build());
            }
        }

        // Purposes
        if (req.getPurposes() != null) {
            List<Purpose> purposes = cacheableService.findPurposes().stream()
                    .filter(c -> req.getPurposes().contains(c.getId()))
                    .toList();
            for (Purpose purpose : purposes) {
                NPurpose nPurpose = repoService.findNPurposeById(purpose);
                hasFeatures.add(HasFeature.builder().feature(nPurpose).build());
            }
        }

        // Dress Codes
        if (req.getDressCodes() != null) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode);
                hasFeatures.add(HasFeature.builder().feature(nDressCode).build());
            }
        }

        // Entertainments
        if (req.getEntertainments() != null) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment);
                hasFeatures.add(HasFeature.builder().feature(nEntertainment).build());
            }
        }

        // Parkings
        if (req.getParkings() != null) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking);
                hasFeatures.add(HasFeature.builder().feature(nParking).build());
            }
        }

        // Prices
        if (req.getPrices() != null) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price);
                hasFeatures.add(HasFeature.builder().feature(nPrice).build());
            }
        }

        // Service Types
        if (req.getServiceTypes() != null) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType);
                hasFeatures.add(HasFeature.builder().feature(nServiceType).build());
            }
        }

        // Spaces
        if (req.getSpaces() != null) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space);
                hasFeatures.add(HasFeature.builder().feature(nSpace).build());
            }
        }

        // Specialties
        if (req.getSpecialties() != null) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty);
                hasFeatures.add(HasFeature.builder().feature(nSpecialty).build());
            }
        }

        // Visit Times
        if (req.getVisitTimes() != null) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime);
                hasFeatures.add(HasFeature.builder().feature(nVisitTime).build());
            }
        }

        // Save updated coffee shop
        CoffeeShop saved = coffeeShopRepository.save(coffeeShop);

        // Update NCoffeeShop
        NCoffeeShop nCoffeeShop = NCoffeeShop.builder()
                .id(saved.getId())
                .name(saved.getName())
                .coverPhoto(saved.getCoverPhoto())
                .hasFeatures(hasFeatures)
                .build();
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


    private String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<ResponseMetaData> contributeCoffeeShop(User user,
                                                                 MultipartFile coverPhoto,
                                                                 MultipartFile[] galleryPhotos,
                                                                 ContributionRequest req) {
        log.info("Start create coffee shop with name = {}",
                 req.getName());
        CoffeeShopContribution contribution = new CoffeeShopContribution();
        req.setGalleryPhotoPaths(imageService.getImagePath(galleryPhotos));
        req.setCoverPhotoPath(cloudinaryService.upload(coverPhoto));
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(req));
        contribution.setSubmittedBy(user);
        contribution.setStatus(Status.PENDING.getValue());
        contribution.setType(ContributionType.CONTRIBUTED.getValue());
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);
        log.info("Finish contribute coffee shop with name = {}, id = {}",
                 req.getName(),
                 saved.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> suggestAnEdit(User user,
                                                          MultipartFile coverPhoto,
                                                          MultipartFile[] galleryPhotos,
                                                          ContributionRequest req,
                                                          Long shopId) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) throw new NotFoundException();
        // Retrieve existing contribution
        CoffeeShopContribution contribution = new CoffeeShopContribution();
        req.addNewGalleryPhotos(imageService.getImagePath(galleryPhotos));
        req.setCoverPhotoPath(coffeeShop.getCoverPhoto());
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(req));
        contribution.setSubmittedBy(user);
        contribution.setStatus(Status.PENDING.getValue());
        contribution.setType(ContributionType.SUGGESTED.getValue());
        contribution.setCoffeeShop(coffeeShop);
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> editContribution(MultipartFile[] galleryPhotos,
                                                             ContributionRequest req,
                                                             Long contributionId) {
        final CoffeeShopContribution contribution = coffeeShopContributionRepository.findByCId(contributionId);
        if (Objects.isNull(contribution)) throw new NotFoundException();
        // Retrieve existing contribution
        req.addNewGalleryPhotos(imageService.getImagePath(galleryPhotos));
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(req));
        if(contribution.getStatus().equalsIgnoreCase(Status.REJECTED.getValue())) {
            contribution.setStatus(Status.PENDING.getValue());
        }
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
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

}
