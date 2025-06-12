package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.postgres.Address;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.ContributionRequest;
import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.request.MoodRequest;
import com.example.socialcoffee.dto.response.CoffeeShopVM;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.MoodCountDto;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Mood;
import com.example.socialcoffee.exception.UnauthorizedException;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.ContributionService;
import com.example.socialcoffee.service.ReviewService;
import com.example.socialcoffee.service.ValidationService;
import com.example.socialcoffee.utils.GeometryUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
public class CoffeeShopController extends BaseController {

    private final CoffeeShopService coffeeShopService;

    private final AddressRepository addressRepository;

    private final ReviewService reviewService;

    private final ValidationService validationService;

    private final ContributionService contributionService;


    @GetMapping(value = "/coffee-shops/sponsored")
    public ResponseEntity<ResponseMetaData> getSponsoredCoffeeShop(@RequestParam(required = false) Double latitude,
                                                                   @RequestParam(required = false) Double longitude) {
        return coffeeShopService.getSponsoredCoffeeShop(latitude,
                longitude);
    }
    @PostMapping(value = "/coffee-shops/contribute", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseMetaData> contributeCoffeeShop(@Valid @ModelAttribute ContributionRequest request,
                                                                 @RequestPart(required = true) MultipartFile coverPhoto,
                                                                 @RequestPart(required = false) MultipartFile[] galleryPhotos) {
        User user = getCurrentUser();
        return contributionService.contributeCoffeeShop(user,
                coverPhoto,
                galleryPhotos,
                request);
    }



    @PutMapping(value = "/coffee-shops/{id}/suggest-an-edit", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseMetaData> suggestCoffeeShop(@ModelAttribute ContributionRequest request,
                                                              @RequestPart(required = false) MultipartFile coverPhoto,
                                                              @RequestPart(required = false) MultipartFile[] galleryPhotos,
                                                              @PathVariable Long id) {
        User user = getCurrentUser();
        return contributionService.suggestAnEdit(user,
                coverPhoto,
                galleryPhotos,
                request,
                id);
    }

    @PutMapping(value = "/coffee-shops/contributions/{contributionId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseMetaData> editContribution(@ModelAttribute ContributionRequest request,
                                                             @RequestPart(required = false) MultipartFile[] galleryPhotos,
                                                             @PathVariable Long contributionId) {
        return contributionService.editContribution(galleryPhotos,
                request,
                contributionId);
    }

    @GetMapping("/coffee-shops/{id}")
    public ResponseEntity<ResponseMetaData> getCoffeeShopById(@PathVariable Long id,
                                                              CoffeeShopSearchRequest filter) {
        User user = null;
        if (SecurityUtil.isAuthenticated()) {
            final Long userId = SecurityUtil.getUserId();
            if (SecurityUtil.isAdmin() || !Objects.equals(userId,
                    NumberUtils.LONG_ZERO)) {
                user = getCurrentUser(userId);
            } else throw new UnauthorizedException();
        }

        return coffeeShopService.getCoffeeShopById(id,
                user,
                filter);
    }

    @GetMapping("/coffee-shops/{id}/suggest-an-edit")
    public ResponseEntity<ResponseMetaData> getCoffeeShopByIdInEdit(@PathVariable Long id) {
        return coffeeShopService.getCoffeeShopByIdInEdit(id);
    }


    @GetMapping("/coffee-shops")
    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng,
            Pageable pageable
    ) {
        return coffeeShopService.getAllCoffeeShop(lat,
                lng,
                pageable);
    }

    @GetMapping("/coffee-shops/search-filters")
    public ResponseEntity<ResponseMetaData> getSearchFilters() {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                coffeeShopService.getSearchFilters()));
    }

    @GetMapping("/coffee-shops/search")
    public ResponseEntity<ResponseMetaData> searchCoffeeShop(CoffeeShopSearchRequest request,
                                                             PageDtoIn pageDtoIn,
                                                             @RequestParam(name = "isFromPrompt", defaultValue = "false", required = false) boolean isFromPrompt) {
        final PageDtoOut<CoffeeShopVM> pageDtoOut = coffeeShopService.search(request,
                pageDtoIn.getPage() - 1,
                pageDtoIn.getSize(),
                Sort.unsorted(), isFromPrompt);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/coffee-shops/search/mood")
    public ResponseEntity<ResponseMetaData> searchCoffeeShop(Mood mood,
                                                             PageDtoIn pageDtoIn) {
        final PageDtoOut<CoffeeShopVM> pageDtoOut = coffeeShopService.searchByMood(
                mood,
                getLatitude(),
                getLongitude(),
                PageRequest.of(pageDtoIn.getPage() - 1,
                        pageDtoIn.getSize()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @GetMapping("/add-location")
    public ResponseEntity<ResponseMetaData> addLocation() {
        final List<Address> all = addressRepository.findAll();
        for (Address address : all) {
            if (address.getLatitude() > address.getLongitude()) {
                double tmp = address.getLatitude();
                address.setLatitude(address.getLongitude());
                address.setLongitude(tmp);
            }
            Point point = GeometryUtil.parseLocation(address.getLongitude(),
                    address.getLatitude());
            if (point == null || point.isEmpty()) {
                System.err.println("Failed to create Point for address ID " + address.getId());
                continue;
            }
            address.setLocation(point);
            addressRepository.save(address);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/coffee-shops/migrate/neo4j")
    public ResponseEntity<ResponseMetaData> migrateCoffeeShops() {
        return coffeeShopService.migrateRelationship();
    }

    @PutMapping("/coffee-shops/{shopId}/like")
    public ResponseEntity<ResponseMetaData> likeCoffeeShops(@PathVariable Long shopId) {
        return coffeeShopService.likeCoffeeShop(shopId,
                getCurrentUser());
    }

    @PutMapping("/coffee-shops/{shopId}/unlike")
    public ResponseEntity<ResponseMetaData> unlikeCoffeeShops(@PathVariable Long shopId) {
        return coffeeShopService.unlikeCoffeeShop(shopId,
                getCurrentUser());
    }

    @PutMapping("/coffee-shops/{shopId}/mood")
    public ResponseEntity<ResponseMetaData> toggleCoffeeShopMood(
            @PathVariable Long shopId,
            @RequestBody MoodRequest moodRequest
    ) {
        coffeeShopService.toggleCoffeeShopMood(shopId,
                SecurityUtil.getUserId(),
                moodRequest.getMood());
        return ResponseEntity.ok().body(new ResponseMetaData(
                new MetaDTO(MetaData.SUCCESS)));
    }

    @GetMapping("/coffee-shops/{shopId}/moods")
    public ResponseEntity<ResponseMetaData> getCoffeeShopMoodCounts(@PathVariable Long shopId) {
        MoodCountDto moodCounts = coffeeShopService.getCoffeeShopMoodCounts(shopId);
        return ResponseEntity.ok().body(new ResponseMetaData(
                new MetaDTO(MetaData.SUCCESS),
                moodCounts
        ));
    }

    @GetMapping("/coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> getReviewByShopId(@PathVariable("shop_id") Long shopId,
                                                              PageDtoIn pageDtoIn) {
        return reviewService.getReviewByShopId(shopId,
                pageDtoIn);
    }

    @PutMapping("/coffee-shops/{shop_id}/review/{review_id}")
    public ResponseEntity<ResponseMetaData> editReview(@PathVariable("shop_id") Long shopId,
                                                       @PathVariable("review_id") Long reviewId,
                                                       @ModelAttribute @RequestPart EditReviewRequest editReviewRequest) {
        return reviewService.editReview(shopId,
                reviewId,
                editReviewRequest);
    }

    @PostMapping("/coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> uploadReview(@PathVariable("shop_id") Long shopId,
                                                         @RequestPart(value = "rating") String rating,
                                                         @RequestPart(value = "content", required = false) String content,
                                                         @RequestPart(value = "is_annonymous", required = false) String isAnonymous,
                                                         @RequestPart(value = "review_id", required = false) String parentId,
                                                         @RequestPart(value = "resource", required = false) MultipartFile[] file) {
        User user = getCurrentUser();
        content = StringUtils.trimToEmpty(content);
        List<MetaDTO> metaDTOList = validationService.validationCommentPost(content,
                file,
                Boolean.TRUE);
        if (!CollectionUtils.isEmpty(metaDTOList)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOList,
                    null));
        }
        return reviewService.uploadReview(user,
                shopId,
                Integer.parseInt(rating),
                content,
                file,
                NumberUtils.toLong(parentId));
    }
}
