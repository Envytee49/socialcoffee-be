package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.postgres.AuthProvider;
import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.Role;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.dto.response.CoffeeShopVM;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.CoffeeShopRecommendationDTO;
import com.example.socialcoffee.repository.neo4j.NCoffeeShopRepository;
import com.example.socialcoffee.repository.neo4j.NUserRepository;
import com.example.socialcoffee.repository.postgres.AuthProviderRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.RoleRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.repository.postgres.feature.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final NCoffeeShopRepository nCoffeeShopRepository;

    private final CoffeeShopRepository coffeeShopRepository;

    private final UserRepository userRepository;

    private final NUserRepository nUserRepository;

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

    @CacheEvict(value = "List<CoffeeShopVM>", key = "#userId")
    public void clearRecommendation(Long userId) {
    }

    @CacheEvict(value = "recommendation", allEntries = true)
    public void clearAllWhenReview() {
    }

    @Cacheable(value = "recommendation", key = "#key + #userId")
    public List<CoffeeShopVM> findBasedOnYourPreferences(String key, Long userId) {
        final Map<Long, Double> ids = nCoffeeShopRepository.findBasedOnYourPreferences(userId, 1)
                .stream().collect(Collectors.toMap(
                        CoffeeShopRecommendationDTO::getShopId,
                        CoffeeShopRecommendationDTO::getScore
                ));
        return coffeeShopRepository.findAllById(ids.keySet()).stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null,
                        ids.get(c.getId())))
                .sorted(Comparator.comparing(CoffeeShopVM::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Cacheable(value = "recommendation", key = "#key + #userId")
    public List<CoffeeShopVM> findYouMayLikeRecommendation(String key, Long userId) {
        final Map<Long, Double> ids = nUserRepository.findYouMayLikeRecommendation(userId, 1)
                .stream().collect(Collectors.toMap(
                        CoffeeShopRecommendationDTO::getShopId,
                        CoffeeShopRecommendationDTO::getScore
                ));
        return coffeeShopRepository.findAllById(ids.keySet()).stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null,
                        ids.get(c.getId())))
                .sorted(Comparator.comparing(CoffeeShopVM::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Cacheable(value = "recommendation", key = "#key + #userId")
    public List<CoffeeShopVM> findLikedByPeopleYouFollow(String key, Long userId) {
        final Map<Long, Double> ids = nUserRepository.findLikedByPeopleYouFollow(userId, 1)
                .stream().collect(Collectors.toMap(
                        CoffeeShopRecommendationDTO::getShopId,
                        CoffeeShopRecommendationDTO::getScore
                ));
        return coffeeShopRepository.findAllById(ids.keySet()).stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null,
                        ids.get(c.getId())))
                .sorted(Comparator.comparing(CoffeeShopVM::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Cacheable(value = "recommendation", key = "#key + #userId")
    public List<CoffeeShopVM> findSimilarToPlacesYouLike(String key, Long userId) {
        final Map<Long, Double> ids = nCoffeeShopRepository.findSimilarToPlacesYouLike(userId)
                .stream().collect(Collectors.toMap(
                        CoffeeShopRecommendationDTO::getShopId,
                        CoffeeShopRecommendationDTO::getScore
                ));
        return coffeeShopRepository.findAllById(ids.keySet()).stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null,
                        ids.get(c.getId())))
                .sorted(Comparator.comparing(CoffeeShopVM::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Cacheable(value = "List<CoffeeShopVM>", key = "'TOP_10_OF_ALL_TIME'")
    public List<CoffeeShopVM> getTop1OfAllTime() {
        final List<CoffeeShop> top10 = coffeeShopRepository.findTop10CoffeeShopsByWeightedRatingAndCollections(1.0,
                2.0,
                4.0,
                8.0,
                16.0);

        return top10
                .stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null))
                .toList();
    }

    @Cacheable(value = "List<CoffeeShopVM>", key = "'TRENDING_THIS_WEEK'")
    public List<CoffeeShopVM> getTrendingThisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Pageable top10 = PageRequest.of(0,
                10);
        final List<CoffeeShop> trendingThisWeek = coffeeShopRepository.findTrendingCoffeeShops(1.0,
                2.0,
                4.0,
                8.0,
                16.0,
                startOfWeek,
                now,
                top10);

        return trendingThisWeek
                .stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null))
                .toList();
    }

    @Cacheable(value = "List<CoffeeShopVM>", key = "'TRENDING_THIS_WEEK'")
    public List<CoffeeShopVM> getTrendingThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1);
        Pageable top10 = PageRequest.of(0,
                10);
        final List<CoffeeShop> trendingThisWeek = coffeeShopRepository.findTrendingCoffeeShops(1.0,
                2.0,
                4.0,
                8.0,
                16.0,
                startOfMonth,
                now,
                top10);

        return trendingThisWeek
                .stream().map(c -> CoffeeShopVM.toVM(c,
                        null,
                        null))
                .toList();
    }

    @Cacheable(value = "List<User>", key = "'ACTIVE_USER'")
    public List<User> getActiveUsers() {
        return userRepository.findByStatus(Status.ACTIVE.getValue()).stream().filter(u -> u.getId() != 0).toList();
    }
}
