package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.request.CreateFeatureRequest;
import com.example.socialcoffee.dto.request.UpdateFeatureRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.service.CacheableService;
import com.example.socialcoffee.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feature")
@RequiredArgsConstructor
public class FeatureController {
    private final FeatureService featureService;

    private final CacheableService cacheableService;

    // Ambiance GET
    @GetMapping("/ambiance")
    public ResponseEntity<ResponseMetaData> getAmbiances() {
        List<Ambiance> data = cacheableService.findAmbiances();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Amenity GET
    @GetMapping("/amenity")
    public ResponseEntity<ResponseMetaData> getAmenities() {
        List<Amenity> data = cacheableService.findAmenities();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Capacity GET
    @GetMapping("/capacity")
    public ResponseEntity<ResponseMetaData> getCapacities() {
        List<Capacity> data = cacheableService.findCapacities();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Category GET
    @GetMapping("/category")
    public ResponseEntity<ResponseMetaData> getCategories() {
        List<Category> data = cacheableService.findCategories();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // DressCode GET
    @GetMapping("/dress-code")
    public ResponseEntity<ResponseMetaData> getDressCodes() {
        List<DressCode> data = cacheableService.findDressCodes();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Entertainment GET
    @GetMapping("/entertainment")
    public ResponseEntity<ResponseMetaData> getEntertainments() {
        List<Entertainment> data = cacheableService.findEntertainments();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Parking GET
    @GetMapping("/parking")
    public ResponseEntity<ResponseMetaData> getParkings() {
        List<Parking> data = cacheableService.findParkings();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Price GET
    @GetMapping("/price")
    public ResponseEntity<ResponseMetaData> getPrices() {
        List<Price> data = cacheableService.findPrices();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // ServiceType GET
    @GetMapping("/service-type")
    public ResponseEntity<ResponseMetaData> getServiceTypes() {
        List<ServiceType> data = cacheableService.findServiceTypes();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Space GET
    @GetMapping("/space")
    public ResponseEntity<ResponseMetaData> getSpaces() {
        List<Space> data = cacheableService.findSpaces();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Specialty GET
    @GetMapping("/specialty")
    public ResponseEntity<ResponseMetaData> getSpecialties() {
        List<Specialty> data = cacheableService.findSpecialties();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // VisitTime GET
    @GetMapping("/visit-time")
    public ResponseEntity<ResponseMetaData> getVisitTimes() {
        List<VisitTime> data = cacheableService.findVisitTimes();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    @GetMapping("/purpose")
    public ResponseEntity<ResponseMetaData> getPurposes() {
        List<Purpose> data = cacheableService.findPurposes();
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      data));
    }

    // Ambiance Controller Methods
    @PostMapping("/ambiance")
    public ResponseEntity<ResponseMetaData> createAmbiance(@RequestBody CreateFeatureRequest req) {
        return featureService.createAmbiance(req.getValue());
    }

    @PutMapping("/ambiance")
    public ResponseEntity<ResponseMetaData> updateAmbiance(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateAmbiance(req);
    }

    @DeleteMapping("/ambiance/{id}")
    public ResponseEntity<ResponseMetaData> deleteAmbiance(@PathVariable Long id) {
        return featureService.deleteAmbiance(id);
    }

    // Amenity Controller Methods
    @PostMapping("/amenity")
    public ResponseEntity<ResponseMetaData> createAmenity(@RequestBody CreateFeatureRequest req) {
        return featureService.createAmenity(req.getValue());
    }

    @PutMapping("/amenity")
    public ResponseEntity<ResponseMetaData> updateAmenity(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateAmenity(req);
    }

    @DeleteMapping("/amenity/{id}")
    public ResponseEntity<ResponseMetaData> deleteAmenity(@PathVariable Long id) {
        return featureService.deleteAmenity(id);
    }

    // Capacity Controller Methods
    @PostMapping("/capacity")
    public ResponseEntity<ResponseMetaData> createCapacity(@RequestBody CreateFeatureRequest req) {
        return featureService.createCapacity(req.getValue());
    }

    @PutMapping("/capacity")
    public ResponseEntity<ResponseMetaData> updateCapacity(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateCapacity(req);
    }

    @DeleteMapping("/capacity/{id}")
    public ResponseEntity<ResponseMetaData> deleteCapacity(@PathVariable Long id) {
        return featureService.deleteCapacity(id);
    }

    // Category Controller Methods
    @PostMapping("/category")
    public ResponseEntity<ResponseMetaData> createCategory(@RequestBody CreateFeatureRequest req) {
        return featureService.createCategory(req.getValue());
    }

    @PutMapping("/category")
    public ResponseEntity<ResponseMetaData> updateCategory(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateCategory(req);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<ResponseMetaData> deleteCategory(@PathVariable Long id) {
        return featureService.deleteCategory(id);
    }

    // DressCode Controller Methods
    @PostMapping("/dress-code")
    public ResponseEntity<ResponseMetaData> createDressCode(@RequestBody CreateFeatureRequest req) {
        return featureService.createDressCode(req.getValue());
    }

    @PutMapping("/dress-code")
    public ResponseEntity<ResponseMetaData> updateDressCode(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateDressCode(req);
    }

    @DeleteMapping("/dress-code/{id}")
    public ResponseEntity<ResponseMetaData> deleteDressCode(@PathVariable Long id) {
        return featureService.deleteDressCode(id);
    }

    // Entertainment Controller Methods
    @PostMapping("/entertainment")
    public ResponseEntity<ResponseMetaData> createEntertainment(@RequestBody CreateFeatureRequest req) {
        return featureService.createEntertainment(req.getValue());
    }

    @PutMapping("/entertainment")
    public ResponseEntity<ResponseMetaData> updateEntertainment(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateEntertainment(req);
    }

    @DeleteMapping("/entertainment/{id}")
    public ResponseEntity<ResponseMetaData> deleteEntertainment(@PathVariable Long id) {
        return featureService.deleteEntertainment(id);
    }

    // Parking Controller Methods
    @PostMapping("/parking")
    public ResponseEntity<ResponseMetaData> createParking(@RequestBody CreateFeatureRequest req) {
        return featureService.createParking(req.getValue());
    }

    @PutMapping("/parking")
    public ResponseEntity<ResponseMetaData> updateParking(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateParking(req);
    }

    @DeleteMapping("/parking/{id}")
    public ResponseEntity<ResponseMetaData> deleteParking(@PathVariable Long id) {
        return featureService.deleteParking(id);
    }

    // Price Controller Methods
    @PostMapping("/price")
    public ResponseEntity<ResponseMetaData> createPrice(@RequestBody CreateFeatureRequest req) {
        return featureService.createPrice(req.getValue());
    }

    @PutMapping("/price")
    public ResponseEntity<ResponseMetaData> updatePrice(@RequestBody UpdateFeatureRequest req) {
        return featureService.updatePrice(req);
    }

    @DeleteMapping("/price/{id}")
    public ResponseEntity<ResponseMetaData> deletePrice(@PathVariable Long id) {
        return featureService.deletePrice(id);
    }

    // ServiceType Controller Methods
    @PostMapping("/service-type")
    public ResponseEntity<ResponseMetaData> createServiceType(@RequestBody CreateFeatureRequest req) {
        return featureService.createServiceType(req.getValue());
    }

    @PutMapping("/service-type")
    public ResponseEntity<ResponseMetaData> updateServiceType(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateServiceType(req);
    }

    @DeleteMapping("/service-type/{id}")
    public ResponseEntity<ResponseMetaData> deleteServiceType(@PathVariable Long id) {
        return featureService.deleteServiceType(id);
    }

    // Space Controller Methods
    @PostMapping("/space")
    public ResponseEntity<ResponseMetaData> createSpace(@RequestBody CreateFeatureRequest req) {
        return featureService.createSpace(req.getValue());
    }

    @PutMapping("/space")
    public ResponseEntity<ResponseMetaData> updateSpace(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateSpace(req);
    }

    @DeleteMapping("/space/{id}")
    public ResponseEntity<ResponseMetaData> deleteSpace(@PathVariable Long id) {
        return featureService.deleteSpace(id);
    }

    // Specialty Controller Methods
    @PostMapping("/specialty")
    public ResponseEntity<ResponseMetaData> createSpecialty(@RequestBody CreateFeatureRequest req) {
        return featureService.createSpecialty(req.getValue());
    }

    @PutMapping("/specialty")
    public ResponseEntity<ResponseMetaData> updateSpecialty(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateSpecialty(req);
    }

    @DeleteMapping("/specialty/{id}")
    public ResponseEntity<ResponseMetaData> deleteSpecialty(@PathVariable Long id) {
        return featureService.deleteSpecialty(id);
    }

    // VisitTime Controller Methods
    @PostMapping("/visit-time")
    public ResponseEntity<ResponseMetaData> createVisitTime(@RequestBody CreateFeatureRequest req) {
        return featureService.createVisitTime(req.getValue());
    }

    @PutMapping("/visit-time")
    public ResponseEntity<ResponseMetaData> updateVisitTime(@RequestBody UpdateFeatureRequest req) {
        return featureService.updateVisitTime(req);
    }

    @DeleteMapping("/visit-time/{id}")
    public ResponseEntity<ResponseMetaData> deleteVisitTime(@PathVariable Long id) {
        return featureService.deleteVisitTime(id);
    }

    //    Purposes
    @PostMapping("/purpose")
    public ResponseEntity<ResponseMetaData> createPurpose(@RequestBody CreateFeatureRequest req) {
        return featureService.createPurpose(req.getValue());
    }

    @PutMapping("/purpose")
    public ResponseEntity<ResponseMetaData> updatePurpose(@RequestBody UpdateFeatureRequest req) {
        return featureService.updatePurpose(req);
    }

    @DeleteMapping("/purpose/{id}")
    public ResponseEntity<ResponseMetaData> deletePurpose(@PathVariable Long id) {
        return featureService.deletePurpose(id);
    }
}
