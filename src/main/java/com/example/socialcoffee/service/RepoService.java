package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.neo4j.feature.*;
import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.Review;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.domain.neo4j.NCoffeeShop;
import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.neo4j.relationship.HasFeature;
import com.example.socialcoffee.repository.neo4j.*;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.ReviewRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
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

    private final ReviewRepository reviewRepository;

    private final Neo4jClient neo4jClient;

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
    public NAmbiance findNAmbianceById(Ambiance ambiance) {
        NAmbiance nAmbiance = new NAmbiance();
        nAmbiance.setId(ambiance.getId());
        nAmbiance.setName(ambiance.getValue());
        return nAmbianceRepository.findById(ambiance.getId())
                .orElse(nAmbianceRepository.save(nAmbiance));
    }

    @Transactional("neo4jTransactionManager")
    public NCategory findNCategoryById(Category category) {
        NCategory nCategory = new NCategory();
        nCategory.setId(category.getId());
        nCategory.setName(category.getValue());
        return nCategoryRepository.findById(category.getId())
                .orElse(nCategoryRepository.save(nCategory));
    }

    @Transactional("neo4jTransactionManager")
    public NAmenity findNAmenityById(Amenity amenity) {
        NAmenity nAmenity = new NAmenity();
        nAmenity.setId(amenity.getId());
        nAmenity.setName(amenity.getValue());
        return nAmenityRepository.findById(amenity.getId())
                .orElse(nAmenityRepository.save(nAmenity));
    }

    @Transactional("neo4jTransactionManager")
    public NCapacity findNCapacityById(Capacity capacity) {
        NCapacity nCapacity = new NCapacity();
        nCapacity.setId(capacity.getId());
        nCapacity.setName(capacity.getValue());
        return nCapacityRepository.findById(capacity.getId())
                .orElse(nCapacityRepository.save(nCapacity));
    }

    @Transactional("neo4jTransactionManager")
    public NDressCode findNDressCodeById(DressCode dressCode) {
        NDressCode nDressCode = new NDressCode();
        nDressCode.setId(dressCode.getId());
        nDressCode.setName(dressCode.getValue());
        return nDressCodeRepository.findById(dressCode.getId())
                .orElse(nDressCodeRepository.save(nDressCode));
    }

    @Transactional("neo4jTransactionManager")
    public NEntertainment findNEntertainmentById(Entertainment entertainment) {
        NEntertainment nEntertainment = new NEntertainment();
        nEntertainment.setId(entertainment.getId());
        nEntertainment.setName(entertainment.getValue());
        return nEntertainmentRepository.findById(entertainment.getId())
                .orElse(nEntertainmentRepository.save(nEntertainment));
    }

    @Transactional("neo4jTransactionManager")
    public NParking findNParkingById(Parking parking) {
        NParking nParking = new NParking();
        nParking.setId(parking.getId());
        nParking.setName(parking.getValue());
        return nParkingRepository.findById(parking.getId())
                .orElse(nParkingRepository.save(nParking));
    }

    @Transactional("neo4jTransactionManager")
    public NPrice findNPriceById(Price price) {
        NPrice nPrice = new NPrice();
        nPrice.setId(price.getId());
        nPrice.setName(price.getValue());
        return nPriceRepository.findById(price.getId())
                .orElse(nPriceRepository.save(nPrice));
    }

    @Transactional("neo4jTransactionManager")
    public NServiceType findNServiceTypeById(ServiceType serviceType) {
        NServiceType nServiceType = new NServiceType();
        nServiceType.setId(serviceType.getId());
        nServiceType.setName(serviceType.getValue());
        return nServiceTypeRepository.findById(serviceType.getId())
                .orElse(nServiceTypeRepository.save(nServiceType));
    }

    @Transactional("neo4jTransactionManager")
    public NSpace findNSpaceById(Space space) {
        NSpace nSpace = new NSpace();
        nSpace.setId(space.getId());
        nSpace.setName(space.getValue());
        return nSpaceRepository.findById(space.getId())
                .orElse(nSpaceRepository.save(nSpace));
    }

    @Transactional("neo4jTransactionManager")
    public NSpecialty findNSpecialtyById(Specialty specialty) {
        NSpecialty nSpecialty = new NSpecialty();
        nSpecialty.setId(specialty.getId());
        nSpecialty.setName(specialty.getValue());
        return nSpecialtyRepository.findById(specialty.getId())
                .orElse(nSpecialtyRepository.save(nSpecialty));
    }

    @Transactional("neo4jTransactionManager")
    public NVisitTime findNVisitTimeById(VisitTime visitTime) {
        NVisitTime nVisitTime = new NVisitTime();
        nVisitTime.setId(visitTime.getId());
        nVisitTime.setName(visitTime.getValue());
        return nVisitTimeRepository.findById(visitTime.getId())
                .orElse(nVisitTimeRepository.save(nVisitTime));
    }

    @Transactional("neo4jTransactionManager")
    public NPurpose findNPurposeById(Purpose purpose) {
        NPurpose nPurpose = new NPurpose();
        nPurpose.setId(purpose.getId());
        nPurpose.setName(purpose.getValue());
        return nPurposeRepository.findById(purpose.getId())
                .orElse(nPurposeRepository.save(nPurpose));
    }

    @Transactional("neo4jTransactionManager")
    public void saveNCoffeeShop(NCoffeeShop nCoffeeShop) {
        nCoffeeShopRepository.save(nCoffeeShop);
    }


    public NCoffeeShop findNCoffeeShopById(Long shopId) {
        return nCoffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException("Coffee Shop " +
                        shopId));
    }

    @Transactional
    public void migrateReviews() {
        log.info("Starting review migration from PostgreSQL to Neo4j");

        int page = 0;
        int BATCH_SIZE = 100;
        Page<Review> reviewPage;
        long totalMigrated = 0;
        long totalSkipped = 0;

        do {
            reviewPage = reviewRepository.findByStatus(Status.ACTIVE.getValue(), PageRequest.of(page, BATCH_SIZE));
            log.info("Processing batch {} with {} reviews", page, reviewPage.getNumberOfElements());

            for (Review pgReview : reviewPage.getContent()) {
                try {
                    // Verify NUser and NCoffeeShop exist in Neo4j
                    Long userId = pgReview.getUser().getId();
                    Long coffeeShopId = pgReview.getCoffeeShop().getId();

                    if (!nUserRepository.existsById(userId)) {
                        log.warn("Skipping review {}: NUser with id {} not found in Neo4j", pgReview.getId(), userId);
                        totalSkipped++;
                        continue;
                    }

                    if (!nCoffeeShopRepository.existsById(coffeeShopId)) {
                        log.warn("Skipping review {}: NCoffeeShop with id {} not found in Neo4j", pgReview.getId(), coffeeShopId);
                        totalSkipped++;
                        continue;
                    }

                    // Create REVIEW relationship in Neo4j
                    Map<String, Object> params = new HashMap<>();
                    params.put("userId", userId);
                    params.put("coffeeShopId", coffeeShopId);
                    params.put("reviewId", pgReview.getId().toString());
                    params.put("rating", pgReview.getRating());
                    params.put("createdAt", pgReview.getCreatedAt());
                    params.put("updatedAt", pgReview.getUpdatedAt());

                    neo4jClient.query(
                            "MATCH (u:User {id: $userId}), (cs:CoffeeShop {id: $coffeeShopId}) " +
                                    "MERGE (u)-[r:REVIEW {id: $reviewId, rating: $rating, createdAt: $createdAt, updatedAt: $updatedAt}]->(cs)"
                    ).bindAll(params).run();

                    totalMigrated++;
                } catch (Exception e) {
                    log.error("Failed to migrate review {}: {}", pgReview.getId(), e.getMessage());
                    totalSkipped++;
                }
            }

            page++;
        } while (reviewPage.hasNext());

        log.info("Migration completed: {} reviews migrated, {} reviews skipped", totalMigrated, totalSkipped);
    }
}
