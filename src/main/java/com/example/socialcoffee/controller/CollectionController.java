package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.request.CollectionRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CollectionController extends BaseController {
    private final CollectionService collectionService;

    @GetMapping("/collections")
    public ResponseEntity<ResponseMetaData> getCollections(@RequestParam(value = "displayName", required = false) String displayName,
                                                           @RequestParam(value = "shopId", required = false) Long coffeeShopId,
                                                           PageDtoIn pageDtoIn) {
        User user = getCurrentUser(displayName);
        return collectionService.getCollections(user,
                                                coffeeShopId,
                                                pageDtoIn);
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<ResponseMetaData> getCollection(@PathVariable Long collectionId,
                                                          @RequestParam(value = "lat", required = false) Double lat,
                                                          @RequestParam(value = "lng", required = false) Double lng) {
        return collectionService.getCollectionById(collectionId, lat, lng);
    }

    @PostMapping(value = "/collections", consumes = "multipart/form-data")
    public ResponseEntity<ResponseMetaData> createNewCollection(@ModelAttribute CollectionRequest request) {
        User user = getCurrentUser();
        if (StringUtils.isBlank(request.getName())) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.INVALID_PARAMETERS)));
        }
        return collectionService.createNewCollection(user,
                                                     request);
    }

    @PutMapping("/collections/{collectionId}")
    public ResponseEntity<ResponseMetaData> addCoffeeShopToCollection(@PathVariable Long collectionId,
                                                                      @RequestParam(value = "shopId") String shopId) {
        return collectionService.addCoffeeShopToCollection(collectionId,
                                                           Long.parseLong(shopId));
    }

    @PatchMapping("/collections/{collectionId}/name")
    public ResponseEntity<ResponseMetaData> updateCollectionName(@PathVariable Long collectionId,
                                                                 @RequestParam(value = "name") String name) {
        return collectionService.updateCollectionName(collectionId,
                                                      name);
    }

    @PatchMapping("/collections/{collectionId}/description")
    public ResponseEntity<ResponseMetaData> updateCollectionDescription(@PathVariable Long collectionId,
                                                                        @RequestParam(value = "description") String description) {
        return collectionService.updateCollectionDescription(collectionId,
                                                             description);
    }

    @DeleteMapping("/collections/{collectionId}")
    public ResponseEntity<ResponseMetaData> removeCoffeeShopFromCollection(@PathVariable Long collectionId,
                                                                           @RequestParam(value = "shopId") String shopId) {
        return collectionService.removeCoffeeShopFromCollection(collectionId,
                                                                Long.parseLong(shopId));
    }
}
