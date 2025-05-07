package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.Address;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.model.CoffeeShopFilter;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.GenerateTextService;
import com.example.socialcoffee.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
public class CoffeeShopController extends BaseController{

    private final CoffeeShopService coffeeShopService;
    private final AddressRepository addressRepository;

    @PostMapping(value = "/coffee-shops", consumes = "multipart/form-data")
    public ResponseEntity<ResponseMetaData> createCoffeeShop(@ModelAttribute CreateCoffeeShopRequest request) {
        User user = getCurrentUser();
        return coffeeShopService.createCoffeeShop(user, request);
    }

    @GetMapping("/coffee-shops/{id}")
    public ResponseEntity<ResponseMetaData> getCoffeeShopById(@PathVariable Long id) {
        return coffeeShopService.getCoffeeShopById(id);
    }


    @GetMapping("/coffee-shops")
    public ResponseEntity<ResponseMetaData> getAllCoffeeShop(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng,
            Pageable pageable
    ) {
        return coffeeShopService.getAllCoffeeShop(lat, lng,pageable);
    }

    @GetMapping("/coffee-shops/search-filters")
    public ResponseEntity<ResponseMetaData> getSearchFilters() {
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             coffeeShopService.getSearchFilters()));
    }

    @GetMapping("/coffee-shops/search")
    public ResponseEntity<ResponseMetaData> searchCoffeeShop(CoffeeShopSearchRequest request, PageDtoIn pageDtoIn) {
        return coffeeShopService.search(request, pageDtoIn.getPage() - 1, pageDtoIn.getSize());
    }

    @GetMapping("/add-location")
    public ResponseEntity<ResponseMetaData> addLocation() {
        final List<Address> all = addressRepository.findAll();
        for (Address address : all) {
            if(address.getLatitude() > address.getLongitude()) {
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

    @GetMapping("/test")
    public CoffeeShopFilter test(@RequestParam String prompt) {
        return coffeeShopService.test(prompt);
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
}
