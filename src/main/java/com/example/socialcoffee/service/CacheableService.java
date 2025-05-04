package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.AuthProvider;
import com.example.socialcoffee.domain.Role;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.repository.postgres.AuthProviderRepository;
import com.example.socialcoffee.repository.postgres.RoleRepository;
import com.example.socialcoffee.repository.postgres.feature.*;
import com.example.socialcoffee.repository.postgres.feature.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheableService {
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
    private final AuthProviderRepository authProviderRepository;
    private final RoleRepository roleRepository;

    @Cacheable(value = "ambiances")
    public List<Ambiance> findAmbiances() {
        return ambianceRepository.findAll();
    }
    @Cacheable(value = "amenities")
    public List<Amenity> findAmenities() {
        return amenityRepository.findAll();
    }

    @Cacheable(value = "capacities")
    public List<Capacity> findCapacities() {
        return capacityRepository.findAll();
    }

    @Cacheable(value = "categories")
    public List<Category> findCategories() {
        return categoryRepository.findAll();
    }

    @Cacheable(value = "dressCodes")
    public List<DressCode> findDressCodes() {
        return dressCodeRepository.findAll();
    }

    @Cacheable(value = "entertainments")
    public List<Entertainment> findEntertainments() {
        return entertainmentRepository.findAll();
    }

    @Cacheable(value = "parkings")
    public List<Parking> findParkings() {
        return parkingRepository.findAll();
    }

    @Cacheable(value = "prices")
    public List<Price> findPrices() {
        return priceRepository.findAll();
    }

    @Cacheable(value = "serviceTypes")
    public List<ServiceType> findServiceTypes() {
        return serviceTypeRepository.findAll();
    }

    @Cacheable(value = "spaces")
    public List<Space> findSpaces() {
        return spaceRepository.findAll();
    }

    @Cacheable(value = "specialties")
    public List<Specialty> findSpecialties() {
        return specialtyRepository.findAll();
    }

    @Cacheable(value = "visitTimes")
    public List<VisitTime> findVisitTimes() {
        return visitTimeRepository.findAll();
    }

    @Cacheable(value = "purposes")
    public List<Purpose> findPurposes() {
        return purposeRepository.findAll();
    }

    @Cacheable(value = "auth", key = "#authProvider")
    public AuthProvider findProvider(String authProvider) {
        return authProviderRepository.findByName(authProvider);
    }

    @Cacheable(value = "role", key = "#role")
    public Role findRole(String role) {
        return roleRepository.findByName(role);
    }
}
