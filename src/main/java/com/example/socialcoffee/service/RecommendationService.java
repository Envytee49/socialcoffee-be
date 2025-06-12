package com.example.socialcoffee.service;

import com.cloudinary.utils.StringUtils;
import com.example.socialcoffee.configuration.ConfigResource;
import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.response.CoffeeShopVM;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.model.CoffeeShopFilter;
import com.example.socialcoffee.repository.neo4j.NCoffeeShopRepository;
import com.example.socialcoffee.repository.neo4j.NUserRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.SecurityUtil;
import com.example.socialcoffee.utils.StringAppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final NCoffeeShopRepository nCoffeeShopRepository;

    private final Neo4jClient neo4jClient;

    private final CacheableService cacheableService;

    private final GroqService groqService;

    private final ObjectMapper objectMapper;

    private final CoffeeShopService coffeeShopService;

    private final NUserRepository nUserRepository;

    private final UserRepository userRepository;

    private final ConfigResource configResource;

    public ResponseEntity<ResponseMetaData> getRelatedCoffeeShop(Long coffeeShopId) {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                nCoffeeShopRepository.findRelatedCoffeeShops(coffeeShopId)));
    }

    public ResponseEntity<ResponseMetaData> findBasedOnYourPreferences(User user) {
        if (StringUtils.isBlank(user.getCoffeePreference())) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NO_CONTENT),
                    new ArrayList<>()));
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.findBasedOnYourPreferences(CommonConstant.PREFERENCE, user.getId())));
    }

    public ResponseEntity<ResponseMetaData> findYouMayLikeRecommendation(User user) {
        final boolean userLike = userRepository.existsUserLike(user.getId());
        if (!userLike)
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NO_CONTENT),
                    new ArrayList<>()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.findYouMayLikeRecommendation(CommonConstant.MAY_LIKE, user.getId())));
    }

    public ResponseEntity<ResponseMetaData> findLikedByPeopleYouFollow(User user) {
        final boolean userFollow = userRepository.existsUserFollow(user.getId());
        if (!userFollow)
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NO_CONTENT),
                    new ArrayList<>()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.findLikedByPeopleYouFollow(CommonConstant.YOU_FOLLOW, user.getId())));
    }

    public ResponseEntity<ResponseMetaData> findSimilarToPlacesYouLike(User user) {
        final boolean userLike = userRepository.existsUserLike(user.getId());
        if (!userLike)
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NO_CONTENT),
                    new ArrayList<>()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.findSimilarToPlacesYouLike(CommonConstant.SIMILAR_PLACE, user.getId())));
    }

    public ResponseEntity<ResponseMetaData> getPeopleWithSameTaste() {
        final List<NUser> similarUsersByLikesAndPreferences = nUserRepository.findSimilarUsersByLikesAndPreferences(SecurityUtil.getUserId());
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                similarUsersByLikesAndPreferences));
    }

    public ResponseEntity<ResponseMetaData> getTop1OfAllTime() {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.getTop1OfAllTime()));
    }

    public ResponseEntity<ResponseMetaData> getTrendingThisWeek() {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.getTrendingThisWeek()));
    }

    public ResponseEntity<ResponseMetaData> getTrendingThisMonth() {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                cacheableService.getTrendingThisMonth()));
    }

    @SneakyThrows
    public ResponseEntity<ResponseMetaData> getRecommendation(String userPrompt) {
        final String systemFilters = objectMapper.writeValueAsString(coffeeShopService.getCoffeeShopFilters());
        String json = groqService.parseFilterFromPrompt(String.format(configResource.prompt(), systemFilters, userPrompt));
        log.info("Returned json: {}, given feature: {}", json, systemFilters);
        final CoffeeShopFilter filter = objectMapper.readValue(StringAppUtils.getJson(json),
                CoffeeShopFilter.class);

        final CoffeeShopSearchRequest searchRequest = filter.toSearchRequest(
                cacheableService.findAmbiances(),
                cacheableService.findAmenities(),
                cacheableService.findCapacities(),
                cacheableService.findEntertainments(),
                cacheableService.findParkings(),
                cacheableService.findPrices(),
                cacheableService.findPurposes(),
                cacheableService.findServiceTypes(),
                cacheableService.findSpaces(),
                cacheableService.findSpecialties(),
                cacheableService.findVisitTimes()
        );
        searchRequest.setMatchedFilter(filter);
        final PageDtoOut<CoffeeShopVM> pageDtoOut = coffeeShopService.search(searchRequest,
                0,
                6,
                Sort.unsorted(), true);
        List<CoffeeShopVM> data = pageDtoOut.getData();
        data.sort(Comparator.comparing(CoffeeShopVM::getAverageRating, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(CoffeeShopVM::getReviewCounts, Comparator.nullsLast(Comparator.reverseOrder())));
        pageDtoOut.setData(data);
        pageDtoOut.setMetaData(searchRequest);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }
}
