package com.example.socialcoffee.service;

import com.example.socialcoffee.dto.request.UpdateFeatureRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.repository.feature.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeatureService {
    private final AmbianceRepository ambianceRepository;
    private final AmenityRepository amenityRepository;
    private final CapacityRepository capacityRepository;
    private final CategoryRepository categoryRepository;
    private final DressCodeRepository dressCodeRepository;
    private final EntertainmentRepository entertainmentRepository;
    private final ParkingRepository parkingRepository;
    private final PriceRepository priceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final SpaceRepository spaceRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VisitTimeRepository visitTimeRepository;
    private final PurposeRepository purposeRepository;

    public ResponseEntity<ResponseMetaData> createAmbiance(String value) {
        Ambiance ambiance = new Ambiance(value);
        ambianceRepository.save(ambiance);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateAmbiance(UpdateFeatureRequest req) {
        Optional<Ambiance> ambianceOptional = ambianceRepository.findById(req.getId());
        if(ambianceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Ambiance ambiance = ambianceOptional.get();
        ambiance.setValue(req.getValue());
        ambianceRepository.save(ambiance);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteAmbiance(Long id) {
        Optional<Ambiance> ambianceOptional = ambianceRepository.findById(id);
        if(ambianceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        ambianceRepository.delete(ambianceOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
    // Amenity Methods
    public ResponseEntity<ResponseMetaData> createAmenity(String value) {
        Amenity amenity = new Amenity(value);
        amenityRepository.save(amenity);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateAmenity(UpdateFeatureRequest req) {
        Optional<Amenity> amenityOptional = amenityRepository.findById(req.getId());
        if(amenityOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Amenity amenity = amenityOptional.get();
        amenity.setValue(req.getValue());
        amenityRepository.save(amenity);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteAmenity(Long id) {
        Optional<Amenity> amenityOptional = amenityRepository.findById(id);
        if(amenityOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        amenityRepository.delete(amenityOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Capacity Methods
    public ResponseEntity<ResponseMetaData> createCapacity(String value) {
        Capacity capacity = new Capacity(value);
        capacityRepository.save(capacity);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateCapacity(UpdateFeatureRequest req) {
        Optional<Capacity> capacityOptional = capacityRepository.findById(req.getId());
        if(capacityOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Capacity capacity = capacityOptional.get();
        capacity.setValue(req.getValue());
        capacityRepository.save(capacity);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteCapacity(Long id) {
        Optional<Capacity> capacityOptional = capacityRepository.findById(id);
        if(capacityOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        capacityRepository.delete(capacityOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Category Methods
    public ResponseEntity<ResponseMetaData> createCategory(String value) {
        Category category = new Category(value);
        categoryRepository.save(category);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateCategory(UpdateFeatureRequest req) {
        Optional<Category> categoryOptional = categoryRepository.findById(req.getId());
        if(categoryOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Category category = categoryOptional.get();
        category.setValue(req.getValue());
        categoryRepository.save(category);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteCategory(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        categoryRepository.delete(categoryOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // DressCode Methods
    public ResponseEntity<ResponseMetaData> createDressCode(String value) {
        DressCode dressCode = new DressCode(value);
        dressCodeRepository.save(dressCode);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateDressCode(UpdateFeatureRequest req) {
        Optional<DressCode> dressCodeOptional = dressCodeRepository.findById(req.getId());
        if(dressCodeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        DressCode dressCode = dressCodeOptional.get();
        dressCode.setValue(req.getValue());
        dressCodeRepository.save(dressCode);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteDressCode(Long id) {
        Optional<DressCode> dressCodeOptional = dressCodeRepository.findById(id);
        if(dressCodeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        dressCodeRepository.delete(dressCodeOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Entertainment Methods
    public ResponseEntity<ResponseMetaData> createEntertainment(String value) {
        Entertainment entertainment = new Entertainment(value);
        entertainmentRepository.save(entertainment);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateEntertainment(UpdateFeatureRequest req) {
        Optional<Entertainment> entertainmentOptional = entertainmentRepository.findById(req.getId());
        if(entertainmentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Entertainment entertainment = entertainmentOptional.get();
        entertainment.setValue(req.getValue());
        entertainmentRepository.save(entertainment);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteEntertainment(Long id) {
        Optional<Entertainment> entertainmentOptional = entertainmentRepository.findById(id);
        if(entertainmentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        entertainmentRepository.delete(entertainmentOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Parking Methods
    public ResponseEntity<ResponseMetaData> createParking(String value) {
        Parking parking = new Parking(value);
        parkingRepository.save(parking);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateParking(UpdateFeatureRequest req) {
        Optional<Parking> parkingOptional = parkingRepository.findById(req.getId());
        if(parkingOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Parking parking = parkingOptional.get();
        parking.setValue(req.getValue());
        parkingRepository.save(parking);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteParking(Long id) {
        Optional<Parking> parkingOptional = parkingRepository.findById(id);
        if(parkingOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        parkingRepository.delete(parkingOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Price Methods
    public ResponseEntity<ResponseMetaData> createPrice(String value) {
        Price price = new Price(value);
        priceRepository.save(price);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updatePrice(UpdateFeatureRequest req) {
        Optional<Price> priceOptional = priceRepository.findById(req.getId());
        if(priceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Price price = priceOptional.get();
        price.setValue(req.getValue());
        priceRepository.save(price);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deletePrice(Long id) {
        Optional<Price> priceOptional = priceRepository.findById(id);
        if(priceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        priceRepository.delete(priceOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // ServiceType Methods
    public ResponseEntity<ResponseMetaData> createServiceType(String value) {
        ServiceType serviceType = new ServiceType(value);
        serviceTypeRepository.save(serviceType);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateServiceType(UpdateFeatureRequest req) {
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(req.getId());
        if(serviceTypeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        ServiceType serviceType = serviceTypeOptional.get();
        serviceType.setValue(req.getValue());
        serviceTypeRepository.save(serviceType);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteServiceType(Long id) {
        Optional<ServiceType> serviceTypeOptional = serviceTypeRepository.findById(id);
        if(serviceTypeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        serviceTypeRepository.delete(serviceTypeOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Space Methods
    public ResponseEntity<ResponseMetaData> createSpace(String value) {
        Space space = new Space(value);
        spaceRepository.save(space);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateSpace(UpdateFeatureRequest req) {
        Optional<Space> spaceOptional = spaceRepository.findById(req.getId());
        if(spaceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Space space = spaceOptional.get();
        space.setValue(req.getValue());
        spaceRepository.save(space);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteSpace(Long id) {
        Optional<Space> spaceOptional = spaceRepository.findById(id);
        if(spaceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        spaceRepository.delete(spaceOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Specialty Methods
    public ResponseEntity<ResponseMetaData> createSpecialty(String value) {
        Specialty specialty = new Specialty(value);
        specialtyRepository.save(specialty);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateSpecialty(UpdateFeatureRequest req) {
        Optional<Specialty> specialtyOptional = specialtyRepository.findById(req.getId());
        if(specialtyOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Specialty specialty = specialtyOptional.get();
        specialty.setValue(req.getValue());
        specialtyRepository.save(specialty);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteSpecialty(Long id) {
        Optional<Specialty> specialtyOptional = specialtyRepository.findById(id);
        if(specialtyOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        specialtyRepository.delete(specialtyOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // VisitTime Methods
    public ResponseEntity<ResponseMetaData> createVisitTime(String value) {
        VisitTime visitTime = new VisitTime(value);
        visitTimeRepository.save(visitTime);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updateVisitTime(UpdateFeatureRequest req) {
        Optional<VisitTime> visitTimeOptional = visitTimeRepository.findById(req.getId());
        if (visitTimeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        VisitTime visitTime = visitTimeOptional.get();
        visitTime.setValue(req.getValue());
        visitTimeRepository.save(visitTime);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
    public ResponseEntity<ResponseMetaData> deleteVisitTime(Long id) {
        Optional<VisitTime> visitTimeOptional = visitTimeRepository.findById(id);
        if(visitTimeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        visitTimeRepository.delete(visitTimeOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    // Purpose Methods
    public ResponseEntity<ResponseMetaData> createPurpose(String value) {
        Purpose purpose = new Purpose(value);
        purposeRepository.save(purpose);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> updatePurpose(UpdateFeatureRequest req) {
        Optional<Purpose> purposeTimeOptional = purposeRepository.findById(req.getId());
        if (purposeTimeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Purpose purpose = purposeTimeOptional.get();
        purpose.setValue(req.getValue());
        purposeRepository.save(purpose);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
    public ResponseEntity<ResponseMetaData> deletePurpose(Long id) {
        Optional<Purpose> purposeTimeOptional = purposeRepository.findById(id);
        if(purposeTimeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        purposeRepository.delete(purposeTimeOptional.get());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
}

