package com.example.socialcoffee.service;

import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.*;
import com.example.socialcoffee.model.CoffeeShopFilter;
import com.example.socialcoffee.neo4j.NCoffeeShop;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.neo4j.feature.*;
import com.example.socialcoffee.neo4j.relationship.HasFeature;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.example.socialcoffee.utils.StringAppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeShopService {
    private final CoffeeShopRepository coffeeShopRepository;
    private final GenerateTextService generateTextService;
    private final AddressRepository addressRepository;
    private final CacheableService cacheableService;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;
    private final RepoService repoService;
    private final ObjectMapper jacksonObjectMapper;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<ResponseMetaData> createCoffeeShop(User user,
                                                             CreateCoffeeShopRequest req) {
        Address address = Address.builder()
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
        log.info("Start create coffee shop with name = {}",
                 req.getName());
        Address savedAddress = addressRepository.save(address);

        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.setName(req.getName());
        coffeeShop.setGalleryPhotos(null);
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
                NAmbiance nAmbiance = repoService.findNAmbianceById(ambiance.getId());
                hasFeatures.add(HasFeature.builder().feature(nAmbiance).build());
            }
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            for (final Amenity amenity : amenities) {
                NAmenity nAmenity = repoService.findNAmenityById(amenity.getId());
                hasFeatures.add(HasFeature.builder().feature(nAmenity).build());
            }
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            for (final Capacity capacity : capacities) {
                NCapacity nCapacity = repoService.findNCapacityById(capacity.getId());
                hasFeatures.add(HasFeature.builder().feature(nCapacity).build());
            }
        }

        if (req.getCategories() != null && !req.getCategories().isEmpty()) {
            List<Category> categories = cacheableService.findCategories().stream()
                    .filter(c -> req.getCategories().contains(c.getId()))
                    .toList();
            for (final Category category : categories) {
                NCategory nCategory = repoService.findNCategoryById(category.getId());
                hasFeatures.add(HasFeature.builder().feature(nCategory).build());
            }
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            for (final DressCode dressCode : dressCodes) {
                NDressCode nDressCode = repoService.findNDressCodeById(dressCode.getId());
                hasFeatures.add(HasFeature.builder().feature(nDressCode).build());
            }
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            for (final Entertainment entertainment : entertainments) {
                NEntertainment nEntertainment = repoService.findNEntertainmentById(entertainment.getId());
                hasFeatures.add(HasFeature.builder().feature(nEntertainment).build());
            }
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            for (final Parking parking : parkings) {
                NParking nParking = repoService.findNParkingById(parking.getId());
                hasFeatures.add(HasFeature.builder().feature(nParking).build());
            }
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            for (final Price price : prices) {
                NPrice nPrice = repoService.findNPriceById(price.getId());
                hasFeatures.add(HasFeature.builder().feature(nPrice).build());
            }
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            for (final ServiceType serviceType : serviceTypes) {
                NServiceType nServiceType = repoService.findNServiceTypeById(serviceType.getId());
                hasFeatures.add(HasFeature.builder().feature(nServiceType).build());
            }
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            for (final Space space : spaces) {
                NSpace nSpace = repoService.findNSpaceById(space.getId());
                hasFeatures.add(HasFeature.builder().feature(nSpace).build());
            }
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            for (final Specialty specialty : specialties) {
                NSpecialty nSpecialty = repoService.findNSpecialtyById(specialty.getId());
                hasFeatures.add(HasFeature.builder().feature(nSpecialty).build());
            }
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            for (final VisitTime visitTime : visitTimes) {
                NVisitTime nVisitTime = repoService.findNVisitTimeById(visitTime.getId());
                hasFeatures.add(HasFeature.builder().feature(nVisitTime).build());
            }
        }

        coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotos()));
        coffeeShop.setCoverPhoto(cloudinaryService.upload(req.getCoverPhoto()));
        coffeeShop.setDescription(req.getDescription());
        String userRole = SecurityUtil.getUserRole();
        if (RoleEnum.ADMIN.getValue().equalsIgnoreCase(userRole)) {
            coffeeShop.setCreatedBy(CommonConstant.ADMIN_INDEX);
            coffeeShop.setStatus(Status.ACTIVE.getValue());
        } else {
            coffeeShop.setStatus(Status.PENDING.getValue());
            coffeeShop.setCreatedBy(user.getId());
        }
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
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      coffeeShop));
    }

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

    public ResponseEntity<ResponseMetaData> getCoffeeShopById(Long id) {
        Optional<CoffeeShop> coffeeShopOptional = coffeeShopRepository.findById(id);
        if (coffeeShopOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST)));
        }
        CoffeeShop coffeeShop = coffeeShopOptional.get();
        coffeeShop.updateGalleryPhotos(Collections.singletonList(Image.builder().url(coffeeShop.getCoverPhoto()).build()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             coffeeShop));
    }

    public ResponseEntity<ResponseMetaData> search(CoffeeShopSearchRequest request,
                                                   Integer page,
                                                   Integer size) {
        Page<CoffeeShop> coffeeShops = coffeeShopRepository.searchCoffeeShops(request,
                                                                              page,
                                                                              size);
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(c -> CoffeeShopVM.toVM(c,
                                                                                           request.getLatitude(),
                                                                                           request.getLongitude()))
                .collect(Collectors.toList());
//        if (ObjectUtils.allNotNull(request.getDistance(),
//                                   request.getLongitude(),
//                                   request.getLatitude())) {
//            coffeeShopVMs.sort(Comparator.comparing(CoffeeShopVM::getDistance));
//        }
        PageDtoOut<CoffeeShopVM> pageDtoOut = PageDtoOut.from(page,
                                                              size,
                                                              coffeeShops.getTotalElements(),
                                                              coffeeShopVMs);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             pageDtoOut));
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
//        searchFilter.setPurposes(cacheableService.findPurposes());
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
//        filter.setPurposes(cacheableService.findPurposes().stream().map(e -> e.getValue()).toList());
        filter.setServiceTypes(cacheableService.findServiceTypes().stream().map(e -> e.getValue()).toList());
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

    @SneakyThrows
    public CoffeeShopFilter test(String prompt) {
        final String s = jacksonObjectMapper.writeValueAsString(getCoffeeShopFilters());
        String json = generateTextService.parseFilterFromPrompt(s,
                                                  CommonConstant.USER_PROMPT + prompt);
        final CoffeeShopFilter filter = jacksonObjectMapper.readValue(StringAppUtils.getJson(json),
                                                                      CoffeeShopFilter.class);
        filter.toSearchRequest(
                cacheableService.findAmbiances(),
                cacheableService.findAmenities(),
                cacheableService.findCapacities(),
                cacheableService.findCategories(),
                cacheableService.findEntertainments(),
                cacheableService.findParkings(),
                cacheableService.findPrices(),
//                cacheableService.findPurposes(),
                cacheableService.findServiceTypes(),
                cacheableService.findSpaces(),
                cacheableService.findSpecialties(),
                cacheableService.findVisitTimes()
        );
        return filter;
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
}
