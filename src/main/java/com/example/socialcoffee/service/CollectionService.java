package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Collection;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.request.CollectionRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.repository.CoffeeShopRepository;
import com.example.socialcoffee.repository.CollectionRepository;
import com.example.socialcoffee.repository.ReviewRepository;
import com.example.socialcoffee.utils.CoffeeShopUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final CoffeeShopRepository coffeeShopRepository;
    private final ReviewRepository reviewRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ResponseEntity<ResponseMetaData> createNewCollection(User user,
                                                                CollectionRequest request) {
        Collection collection = Collection.builder()
                .description(request.getDescription())
                .name(request.getName())
                .user(user)
                .build();
        if (Objects.nonNull(request.getFile())) {
            collection.setCoverUrl(cloudinaryService.upload(request.getFile()));
        }
        collectionRepository.save(collection);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> addCoffeeShopToCollection(Long collectionId,
                                                                      Long shopId) {
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);
        if (optionalCollection.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Collection collection = optionalCollection.get();
        collection.addCoffeeShop(coffeeShop);
        collectionRepository.save(collection);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> removeCoffeeShopFromCollection(Long collectionId,
                                                                      Long shopId) {
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);
        if (optionalCollection.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Collection collection = optionalCollection.get();
        collection.removeCoffeeShop(coffeeShop);
        collectionRepository.save(collection);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> getCollections(User user,
                                                           Long coffeeShopId,
                                                           PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1,
                                           pageDtoIn.getSize());
        CoffeeShop coffeeShop = null;
        if (Objects.nonNull(coffeeShopId)) {
            coffeeShop = coffeeShopRepository.findByShopId(coffeeShopId);
            if (Objects.isNull(coffeeShop))
                return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        final List<Collection> collections = collectionRepository.findByUser(user,
                                                                             pageable);
        final CoffeeShop finalCoffeeShop = coffeeShop;
        final List<CollectionVM> collectionVMs = collections.stream().map(c -> new CollectionVM(c,
                                                                                                finalCoffeeShop))
                .toList();
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             collectionVMs));
    }

    public ResponseEntity<ResponseMetaData> getCollectionById(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (Objects.isNull(collection)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        final List<Long> shopIds = collection.getCoffeeShops().stream().map(CoffeeShop::getId).toList();
        final List<Object[]> averageRatings = reviewRepository.getAverageRatingByCoffeeShopId(shopIds);
        Map<Long, Pair<Double, Long>> reviewSummaries = CoffeeShopUtil.toRatingMap(averageRatings);
        CollectionDetailVM collectionDetailVM = new CollectionDetailVM(collection,
                                                                       collection
                                                                               .getCoffeeShops()
                                                                               .stream().map(c -> CoffeeShopVM.toVM(c, reviewSummaries))
                                                                               .toList());

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             collectionDetailVM));
    }


    public ResponseEntity<ResponseMetaData> updateCollectionName(Long collectionId,
                                                                 String name) {
        if (!StringUtils.hasText(name)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.INVALID_PARAMETERS)));
        }

        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (Objects.isNull(collection)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }

        if (name.equals(collection.getName())) {
            return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
        }

        collection.setName(name);
        collectionRepository.save(collection);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateCollectionDescription(Long collectionId,
                                                                        String description) {
        if (!StringUtils.hasText(description)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.INVALID_PARAMETERS)));
        }

        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (Objects.isNull(collection)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }

        if (description.equals(collection.getDescription())) {
            return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
        }

        collection.setDescription(description);
        collectionRepository.save(collection);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
}
