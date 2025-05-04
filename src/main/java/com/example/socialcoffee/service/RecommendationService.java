package com.example.socialcoffee.service;

import com.cloudinary.utils.StringUtils;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.repository.neo4j.NCoffeeShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final NCoffeeShopRepository nCoffeeShopRepository;

    private final Neo4jClient neo4jClient;

    public ResponseEntity<ResponseMetaData> getRelatedCoffeeShop(Long coffeeShopId) {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             nCoffeeShopRepository.findRelatedCoffeeShops(coffeeShopId)));
    }

    public ResponseEntity<ResponseMetaData> getRecommendationForYou(User user) {
        if (StringUtils.isBlank(user.getCoffeePreference())) {
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.NO_CONTENT), new ArrayList<>()));
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             nCoffeeShopRepository.findRecommendedForYou(user.getId())));
    }

//    public ResponseEntity<ResponseMetaData> getRelatedUsers() {
//
//    }
//
//    public ResponseEntity<ResponseMetaData> getTop10() {
//
//    }
//
//    public ResponseEntity<ResponseMetaData> getTrendingThisWeek() {
//
//    }
//
//    public ResponseEntity<ResponseMetaData> getTrendingThisMonth() {
//
//    }
//
//    public ResponseEntity<ResponseMetaData> getRecommendedForYou(User user) {
//        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
//                                                             nCoffeeShopRepository.findRelatedCoffeeShops(user.getId())));
//    }
//
//    public ResponseEntity<ResponseMetaData> getCoffeeShopsByUserPreference() {
//
//    }
}
