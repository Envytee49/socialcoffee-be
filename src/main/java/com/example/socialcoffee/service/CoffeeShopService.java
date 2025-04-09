package com.example.socialcoffee.service;

import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.*;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.repository.AddressRepository;
import com.example.socialcoffee.repository.CoffeeShopRepository;
import com.example.socialcoffee.repository.DescriptionEmbeddingRepository;
import com.example.socialcoffee.repository.specification.CoffeeShopSpecification;
import com.example.socialcoffee.utils.SecurityUtil;
import com.example.socialcoffee.utils.StringAppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final SentenceTransformerService sentenceTransformerService;
    private final AddressRepository addressRepository;
    private final CacheableService cacheableService;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;
    private final ModelMapper modelMapper;
    private final DescriptionEmbeddingRepository descriptionEmbeddingRepository;

    @Transactional
    public ResponseEntity<ResponseMetaData> createCoffeeShop(CreateCoffeeShopRequest req) {
        Address address = Address.builder()
                .googleMapUrl(req.getGoogleMapUrl())
                .addressDetail(req.getAddressDetail())
                .province(req.getProvince())
                .district(req.getDistrict())
                .ward(req.getWard())
                .longitude(req.getLongitude())
                .latitude(req.getLatitude())
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

        if (req.getAmbiances() != null && !req.getAmbiances().isEmpty()) {
            List<Ambiance> ambiances = cacheableService.findAmbiances().stream()
                    .filter(a -> req.getAmbiances().contains(a.getId()))
                    .toList();
            coffeeShop.setAmbiances(ambiances);
        }

        if (req.getAmenities() != null && !req.getAmenities().isEmpty()) {
            List<Amenity> amenities = cacheableService.findAmenities().stream()
                    .filter(a -> req.getAmenities().contains(a.getId()))
                    .toList();
            coffeeShop.setAmenities(amenities);
        }

        if (req.getCapacities() != null && !req.getCapacities().isEmpty()) {
            List<Capacity> capacities = cacheableService.findCapacities().stream()
                    .filter(c -> req.getCapacities().contains(c.getId()))
                    .toList();
            coffeeShop.setCapacities(capacities);
        }

        if (req.getCategories() != null && !req.getCategories().isEmpty()) {
            List<Category> categories = cacheableService.findCategories().stream()
                    .filter(c -> req.getCategories().contains(c.getId()))
                    .toList();
            coffeeShop.setCategories(categories);
        }

        if (req.getDressCodes() != null && !req.getDressCodes().isEmpty()) {
            List<DressCode> dressCodes = cacheableService.findDressCodes().stream()
                    .filter(d -> req.getDressCodes().contains(d.getId()))
                    .toList();
            coffeeShop.setDressCodes(dressCodes);
        }

        if (req.getEntertainments() != null && !req.getEntertainments().isEmpty()) {
            List<Entertainment> entertainments = cacheableService.findEntertainments().stream()
                    .filter(e -> req.getEntertainments().contains(e.getId()))
                    .toList();
            coffeeShop.setEntertainments(entertainments);
        }

        if (req.getParkings() != null && !req.getParkings().isEmpty()) {
            List<Parking> parkings = cacheableService.findParkings().stream()
                    .filter(p -> req.getParkings().contains(p.getId()))
                    .toList();
            coffeeShop.setParkings(parkings);
        }

        if (req.getPrices() != null && !req.getPrices().isEmpty()) {
            List<Price> prices = cacheableService.findPrices().stream()
                    .filter(p -> req.getPrices().contains(p.getId()))
                    .toList();
            coffeeShop.setPrices(prices);
        }

        if (req.getServiceTypes() != null && !req.getServiceTypes().isEmpty()) {
            List<ServiceType> serviceTypes = cacheableService.findServiceTypes().stream()
                    .filter(s -> req.getServiceTypes().contains(s.getId()))
                    .toList();
            coffeeShop.setServiceTypes(serviceTypes);
        }

        if (req.getSpaces() != null && !req.getSpaces().isEmpty()) {
            List<Space> spaces = cacheableService.findSpaces().stream()
                    .filter(s -> req.getSpaces().contains(s.getId()))
                    .toList();
            coffeeShop.setSpaces(spaces);
        }

        if (req.getSpecialties() != null && !req.getSpecialties().isEmpty()) {
            List<Specialty> specialties = cacheableService.findSpecialties().stream()
                    .filter(s -> req.getSpecialties().contains(s.getId()))
                    .toList();
            coffeeShop.setSpecialties(specialties);
        }

        if (req.getVisitTimes() != null && !req.getVisitTimes().isEmpty()) {
            List<VisitTime> visitTimes = cacheableService.findVisitTimes().stream()
                    .filter(v -> req.getVisitTimes().contains(v.getId()))
                    .toList();
            coffeeShop.setVisitTimes(visitTimes);
        }
        coffeeShop.setGalleryPhotos(imageService.save(req.getGalleryPhotos()));
        coffeeShop.setCoverPhoto(cloudinaryService.upload(req.getCoverPhoto()));
        coffeeShop.setDescription(generateTextService.generateDescription(coffeeShop.featureToString()));
        String userRole = SecurityUtil.getUserRole();
        if(RoleEnum.ADMIN.getValue().equalsIgnoreCase(userRole)){
            coffeeShop.setCreatedBy(CommonConstant.ADMIN_INDEX);
            coffeeShop.setStatus(Status.ACTIVE.getValue());
        } else {
            coffeeShop.setStatus(Status.PENDING.getValue());
            coffeeShop.setCreatedBy(SecurityUtil.getUserId());
        }
        CoffeeShop saved = coffeeShopRepository.save(coffeeShop);
        generateEmbeddingDescription(saved);
        log.info("Finish create coffee shop with name = {}, id = {}",
                 req.getName(),
                 coffeeShop.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      coffeeShop));
    }

    private void generateEmbeddingDescription(CoffeeShop coffeeShop) {
        try {
            Float[] embeddingDescription = sentenceTransformerService.generateEmbeddingDescription(StringAppUtils.removeNewLineCharacter(coffeeShop.getDescription()));
            DescriptionEmbedding descriptionEmbedding = DescriptionEmbedding.builder()
                    .descriptionEmbedding(embeddingDescription)
                    .coffeeShop(coffeeShop)
                    .build();
            descriptionEmbeddingRepository.save(descriptionEmbedding);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public ResponseEntity<ResponseMetaData> getRecommendation(String prompt) {
//        coffeeShopRepository.findAllCoffeeShops();
        Float[] embeddingPrompt = sentenceTransformerService.generateEmbeddingDescription(prompt);
        List<Object[]> coffeeShops = coffeeShopRepository.findSimilarCoffeeShops(Arrays.toString(embeddingPrompt),
                                                                                 5);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      coffeeShops));
    }

    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(final Pageable pageable) {
        final List<CoffeeShop> coffeeShops = coffeeShopRepository.findAll(pageable).getContent();
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(CoffeeShopVM::toVM).toList();
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
                                                   Pageable pageable) {
        Specification<CoffeeShop> spec = CoffeeShopSpecification.searchCoffeeShops(request);
        final Page<CoffeeShop> coffeeShops = coffeeShopRepository.findAll(spec,
                                                                          pageable);
        List<CoffeeShopVM> coffeeShopVMs = coffeeShops.stream().map(CoffeeShopVM::toVM).toList();
        PageDtoOut<CoffeeShopVM> pageDtoOut = PageDtoOut.from(pageable.getPageNumber(),
                                                              pageable.getPageSize(),
                                                              coffeeShops.getTotalElements(),
                                                              coffeeShopVMs);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             pageDtoOut));
    }

    public ResponseEntity<ResponseMetaData> getSearchFilters() {
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
        searchFilter.setDistances(Arrays.stream(Distance.values()).map(d -> new SearchFilter.DistanceDTO((long) d.ordinal(), d.getValue())).collect(Collectors.toList()));
        searchFilter.setSorts(Arrays.stream(CoffeeShopSort.values()).map(s -> new SearchFilter.SortDTO((long) s.ordinal(), s.getValue())).collect(Collectors.toList()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             searchFilter));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> updateCoffeeShopStatus(Long shopId, String newStatus) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if(Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }

        if (!Status.PENDING.getValue().equals(coffeeShop.getStatus())) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST)));
        }
        coffeeShop.setStatus(newStatus);
        coffeeShopRepository.save(coffeeShop);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public Page<CoffeeShopDTO> findCoffeeShops(String name, String status, Pageable pageable) {
        String statusValue = StringUtils.isBlank(status) ? status : null;

        Page<CoffeeShop> coffeeShops;
        if (name != null && statusValue != null) {
            coffeeShops = coffeeShopRepository.findByNameContainingIgnoreCaseAndStatus(name, statusValue, pageable);
        } else if (name != null) {
            coffeeShops = coffeeShopRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (statusValue != null) {
            coffeeShops = coffeeShopRepository.findByStatus(statusValue, pageable);
        } else {
            coffeeShops = coffeeShopRepository.findAll(pageable);
        }

        return coffeeShops.map(CoffeeShop::toCoffeeShopDTO);
    }
}
