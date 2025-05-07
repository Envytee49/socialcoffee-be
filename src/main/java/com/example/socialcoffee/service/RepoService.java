package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.neo4j.NCoffeeShop;
import com.example.socialcoffee.neo4j.NUser;
import com.example.socialcoffee.neo4j.feature.*;
import com.example.socialcoffee.neo4j.relationship.HasFeature;
import com.example.socialcoffee.repository.neo4j.*;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RepoService {
    private final UserRepository userRepository;
    private final NUserRepository nUserRepository;
    private final CoffeeShopRepository coffeeShopRepository;
    private final NCoffeeShopRepository nCoffeeShopRepository;
    private final NFeatureRepository nFeatureRepository;
    private final NAmbianceRepository nAmbianceRepository;
    private final NAmenityRepository nAmenityRepository;
    private final NCapacityRepository nCapacityRepository;
    private final NDressCodeRepository nDressCodeRepository;
    private final NEntertainmentRepository nEntertainmentRepository;
    private final NParkingRepository nParkingRepository;
    private final NPriceRepository nPriceRepository;
    private final NServiceTypeRepository nServiceTypeRepository;
    private final NSpaceRepository nSpaceRepository;
    private final NSpecialtyRepository nSpecialtyRepository;
    private final NVisitTimeRepository nVisitTimeRepository;
    private final NCategoryRepository nCategoryRepository;
    private final NPurposeRepository nPurposeRepository;

    @Transactional(value = "postgresTransactionManager")
    public List<User> fetchUsersFromPostgres() {
        return userRepository.findAll();
    }

    @Transactional(value = "neo4jTransactionManager")
    public void saveUsersToNeo4j(List<NUser> nUsers) {
        nUserRepository.saveAll(nUsers);
    }

    @Transactional(value = "postgresTransactionManager")
    public List<CoffeeShop> fetchCoffeeShopsFromPostgres() {
        return coffeeShopRepository.findAll();
    }

    @Transactional(value = "neo4jTransactionManager")
    public void saveCoffeeShopsToNeo4j(List<NCoffeeShop> nCoffeeShops) {
        nCoffeeShopRepository.saveAll(nCoffeeShops);
    }

    @Transactional("neo4jTransactionManager")
    public void migrateSingleCoffeeShop(CoffeeShop coffeeShop) {
        NCoffeeShop nCoffeeShop = NCoffeeShop.builder()
                .id(coffeeShop.getId())
                .name(coffeeShop.getName())
                .coverPhoto(coffeeShop.getCoverPhoto())
                .build();

        Set<HasFeature> hasFeatures = new HashSet<>();

        if (coffeeShop.getAmbiances() != null) {
            for (Ambiance ambiance : coffeeShop.getAmbiances()) {
                NAmbiance nAmbiance = new NAmbiance();
                nAmbiance.setId(ambiance.getId());
                nAmbiance.setName(ambiance.getValue());
                nFeatureRepository.save(nAmbiance);
                hasFeatures.add(HasFeature.builder().feature(nAmbiance).build());
            }
        }

        if (coffeeShop.getAmenities() != null) {
            for (Amenity amenity : coffeeShop.getAmenities()) {
                NAmenity nAmenity = new NAmenity();
                nAmenity.setId(amenity.getId());
                nAmenity.setName(amenity.getValue());
                nFeatureRepository.save(nAmenity);
                hasFeatures.add(HasFeature.builder().feature(nAmenity).build());
            }
        }

        if (coffeeShop.getCapacities() != null) {
            for (Capacity capacity : coffeeShop.getCapacities()) {
                NCapacity nCapacity = new NCapacity();
                nCapacity.setId(capacity.getId());
                nCapacity.setName(capacity.getValue());
                nFeatureRepository.save(nCapacity);
                hasFeatures.add(HasFeature.builder().feature(nCapacity).build());
            }
        }

        if (coffeeShop.getCategories() != null) {
            for (Category category : coffeeShop.getCategories()) {
                NCategory nCategory = new NCategory();
                nCategory.setId(category.getId());
                nCategory.setName(category.getValue());
                nFeatureRepository.save(nCategory);
                hasFeatures.add(HasFeature.builder().feature(nCategory).build());
            }
        }

        if (coffeeShop.getDressCodes() != null) {
            for (DressCode dressCode : coffeeShop.getDressCodes()) {
                NDressCode nDressCode = new NDressCode();
                nDressCode.setId(dressCode.getId());
                nDressCode.setName(dressCode.getValue());
                nFeatureRepository.save(nDressCode);
                hasFeatures.add(HasFeature.builder().feature(nDressCode).build());
            }
        }

        if (coffeeShop.getEntertainments() != null) {
            for (Entertainment entertainment : coffeeShop.getEntertainments()) {
                NEntertainment nEntertainment = new NEntertainment();
                nEntertainment.setId(entertainment.getId());
                nEntertainment.setName(entertainment.getValue());
                nFeatureRepository.save(nEntertainment);
                hasFeatures.add(HasFeature.builder().feature(nEntertainment).build());
            }
        }

        if (coffeeShop.getParkings() != null) {
            for (Parking parking : coffeeShop.getParkings()) {
                NParking nParking = new NParking();
                nParking.setId(parking.getId());
                nParking.setName(parking.getValue());
                nFeatureRepository.save(nParking);
                hasFeatures.add(HasFeature.builder().feature(nParking).build());
            }
        }

        if (coffeeShop.getPrices() != null) {
            for (Price price : coffeeShop.getPrices()) {
                NPrice nPrice = new NPrice();
                nPrice.setId(price.getId());
                nPrice.setName(price.getValue());
                nFeatureRepository.save(nPrice);
                hasFeatures.add(HasFeature.builder().feature(nPrice).build());
            }
        }

        if (coffeeShop.getServiceTypes() != null) {
            for (ServiceType serviceType : coffeeShop.getServiceTypes()) {
                NServiceType nServiceType = new NServiceType();
                nServiceType.setId(serviceType.getId());
                nServiceType.setName(serviceType.getValue());
                nFeatureRepository.save(nServiceType);
                hasFeatures.add(HasFeature.builder().feature(nServiceType).build());
            }
        }

        if (coffeeShop.getSpaces() != null) {
            for (Space space : coffeeShop.getSpaces()) {
                NSpace nSpace = new NSpace();
                nSpace.setId(space.getId());
                nSpace.setName(space.getValue());
                nFeatureRepository.save(nSpace);
                hasFeatures.add(HasFeature.builder().feature(nSpace).build());
            }
        }

        if (coffeeShop.getSpecialties() != null) {
            for (Specialty specialty : coffeeShop.getSpecialties()) {
                NSpecialty nSpecialty = new NSpecialty();
                nSpecialty.setId(specialty.getId());
                nSpecialty.setName(specialty.getValue());
                nFeatureRepository.save(nSpecialty);
                hasFeatures.add(HasFeature.builder().feature(nSpecialty).build());
            }
        }

        if (coffeeShop.getVisitTimes() != null) {
            for (VisitTime visitTime : coffeeShop.getVisitTimes()) {
                NVisitTime nVisitTime = new NVisitTime();
                nVisitTime.setId(visitTime.getId());
                nVisitTime.setName(visitTime.getValue());
                nFeatureRepository.save(nVisitTime);
                hasFeatures.add(HasFeature.builder().feature(nVisitTime).build());
            }
        }

        nCoffeeShop.setHasFeatures(hasFeatures);
        nCoffeeShopRepository.save(nCoffeeShop);
    }

    @Transactional("neo4jTransactionManager")
    public NUser findNUserById(Long id) {
        return nUserRepository.findById(id).get();
    }

    @Transactional("neo4jTransactionManager")
    public void saveNUser(NUser nUser) {
        nUserRepository.save(nUser);
    }

    @Transactional("neo4jTransactionManager")
    public NAmbiance findNAmbianceById(Long id) {
        return nAmbianceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ambiance " + id));
    }

    @Transactional("neo4jTransactionManager")
    public NCategory findNCategoryById(Long id) {
        return nCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ambiance " + id));
    }

    @Transactional("neo4jTransactionManager")
    public NAmenity findNAmenityById(Long id) {
        return nAmenityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Amenity" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NCapacity findNCapacityById(Long id) {
        return nCapacityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Capacity" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NDressCode findNDressCodeById(Long id) {
        return nDressCodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DressCode" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NEntertainment findNEntertainmentById(Long id) {
        return nEntertainmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entertainment" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NParking findNParkingById(Long id) {
        return nParkingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parking" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NPrice findNPriceById(Long id) {
        return nPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NServiceType findNServiceTypeById(Long id) {
        return nServiceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ServiceType" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NSpace findNSpaceById(Long id) {
        return nSpaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Space" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NSpecialty findNSpecialtyById(Long id) {
        return nSpecialtyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialty" + id));
    }

    @Transactional("neo4jTransactionManager")
    public NVisitTime findNVisitTimeById(Long id) {
        return nVisitTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("VisitTime" + id));
    }

    @Transactional("neo4jTransactionManager")
    public void saveNCoffeeShop(NCoffeeShop nCoffeeShop) {
        nCoffeeShopRepository.save(nCoffeeShop);
    }

    public NPurpose findNPurposeById(Long id) {
        return nPurposeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purpose" + id));
    }

    public NCoffeeShop findNCoffeeShopById(Long shopId) {
        return nCoffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException("Coffee Shop " +
                                                                 shopId));
    }
}
