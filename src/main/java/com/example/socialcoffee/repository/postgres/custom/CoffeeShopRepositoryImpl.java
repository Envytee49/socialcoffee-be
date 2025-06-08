package com.example.socialcoffee.repository.postgres.custom;

import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.Review;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.enums.CoffeeShopSort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CoffeeShopRepositoryImpl implements CoffeeShopRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CoffeeShop> searchCoffeeShops(CoffeeShopSearchRequest request,
                                              Integer page,
                                              Integer size,
                                              Sort sort,
                                              boolean isFromPrompt) {
        Pageable pageable = PageRequest.of(page,
                size,
                sort);

        Long totalCount = isFromPrompt ? getTotalCountForPrompt(request) : getTotalCount(request);

        // Get the paginated results
        List<CoffeeShop> coffeeShops = isFromPrompt ? getPagedResultsForPrompt(request, pageable)
                : getPagedResults(request, pageable);

        // Return PageImpl with results, pageable, and total count
        return new PageImpl<>(coffeeShops,
                pageable,
                totalCount);
    }

    private Long getTotalCountForPrompt(CoffeeShopSearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<CoffeeShop> root = countQuery.from(CoffeeShop.class);

        // Apply the same predicates as the main query
        Predicate predicate = buildPredicate(request,
                criteriaBuilder,
                root, true);

        // Count distinct IDs to handle joins properly
        countQuery.select(criteriaBuilder.countDistinct(root.get("id")));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Long getTotalCount(CoffeeShopSearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<CoffeeShop> root = countQuery.from(CoffeeShop.class);

        // Apply the same predicates as the main query
        Predicate predicate = buildPredicate(request,
                criteriaBuilder,
                root, false);
        List<Join<CoffeeShop, ?>> listJoins = new ArrayList<>();

        // Join each list filter if present
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            listJoins.add(root.join("ambiances", JoinType.INNER));
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            listJoins.add(root.join("amenities", JoinType.INNER));
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            listJoins.add(root.join("capacities", JoinType.INNER));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            listJoins.add(root.join("categories", JoinType.INNER));
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            listJoins.add(root.join("entertainments", JoinType.INNER));
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            listJoins.add(root.join("parkings", JoinType.INNER));
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            listJoins.add(root.join("prices", JoinType.INNER));
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            listJoins.add(root.join("purposes", JoinType.INNER));
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            listJoins.add(root.join("serviceTypes", JoinType.INNER));
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            listJoins.add(root.join("spaces", JoinType.INNER));
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            listJoins.add(root.join("specialties", JoinType.INNER));
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            listJoins.add(root.join("visitTimes", JoinType.INNER));
        }

        // Collect predicates (base + in-list predicates)
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(predicate);

        int joinIndex = 0;
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getAmbiances()));
            joinIndex++;
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getAmenities()));
            joinIndex++;
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getCapacities()));
            joinIndex++;
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getCategories()));
            joinIndex++;
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getEntertainments()));
            joinIndex++;
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getParkings()));
            joinIndex++;
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getPrices()));
            joinIndex++;
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getPurposes()));
            joinIndex++;
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getServiceTypes()));
            joinIndex++;
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getSpaces()));
            joinIndex++;
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getSpecialties()));
            joinIndex++;
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getVisitTimes()));
            joinIndex++;
        }

        countQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Group by coffee shop ID
        countQuery.groupBy(root.get("id"));

        // HAVING: each join count distinct ids = filter list size (match all)
        List<Predicate> havingPredicates = new ArrayList<>();
        joinIndex = 0;
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getAmbiances().size()));
            joinIndex++;
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getAmenities().size()));
            joinIndex++;
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getCapacities().size()));
            joinIndex++;
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getCategories().size()));
            joinIndex++;
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getEntertainments().size()));
            joinIndex++;
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getParkings().size()));
            joinIndex++;
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getPrices().size()));
            joinIndex++;
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getPurposes().size()));
            joinIndex++;
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getServiceTypes().size()));
            joinIndex++;
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getSpaces().size()));
            joinIndex++;
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getSpecialties().size()));
            joinIndex++;
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getVisitTimes().size()));
            joinIndex++;
        }

        if (!havingPredicates.isEmpty()) {
            countQuery.having(havingPredicates.toArray(new Predicate[0]));
        }

        // Count distinct IDs to handle joins properly
        countQuery.select(criteriaBuilder.countDistinct(root.get("id")));
        return (long) entityManager.createQuery(countQuery).getResultList().size();
    }

    private List<CoffeeShop> getPagedResultsForPrompt(CoffeeShopSearchRequest request,
                                                      Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CoffeeShop> criteriaQuery = criteriaBuilder.createQuery(CoffeeShop.class);
        Root<CoffeeShop> root = criteriaQuery.from(CoffeeShop.class);

        // Apply predicates
        Predicate predicate = buildPredicate(request,
                criteriaBuilder,
                root, true);
        criteriaQuery.where(predicate);
        Sort sort = pageable.getSort();
        List<Order> orders = new ArrayList<>();

        for (Sort.Order sortOrder : sort) {
            if (sortOrder.isAscending()) {
                orders.add(criteriaBuilder.asc(root.get(sortOrder.getProperty())));
            } else {
                orders.add(criteriaBuilder.desc(root.get(sortOrder.getProperty())));
            }
        }

        criteriaQuery.orderBy(orders);

        // Apply sorting
        if (request.getSort() != null) {
            Join<CoffeeShop, Review> coffeeShopReviews = root.join("reviews",
                    JoinType.LEFT);
            criteriaQuery.groupBy(root.get("id"));

            if (CoffeeShopSort.HIGHEST_RATED.getValue().equals(request.getSort())) {
                // Handle NULL values with COALESCE for average rating
                Expression<Double> averageRating = criteriaBuilder.coalesce(
                        criteriaBuilder.avg(coffeeShopReviews.get("rating")),
                        0.0
                );
                criteriaQuery.orderBy(criteriaBuilder.desc(averageRating));
            } else if (CoffeeShopSort.MOST_REVIEW.getValue().equals(request.getSort())) {
                Expression<Long> reviewCount = criteriaBuilder.count(coffeeShopReviews.get("id"));
                criteriaQuery.orderBy(criteriaBuilder.desc(reviewCount));
            } else {
                // Default sorting if needed
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
            }
        }

        // Create the typed query and apply pagination
        TypedQuery<CoffeeShop> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Execute and return results
        return typedQuery.getResultList();
    }

    private List<CoffeeShop> getPagedResults(CoffeeShopSearchRequest request,
                                             Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CoffeeShop> criteriaQuery = criteriaBuilder.createQuery(CoffeeShop.class);
        Root<CoffeeShop> root = criteriaQuery.from(CoffeeShop.class);

        // Apply predicates
        Predicate predicate = buildPredicate(request,
                criteriaBuilder,
                root, false);
        List<Join<CoffeeShop, ?>> listJoins = new ArrayList<>();
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            listJoins.add(root.join("ambiances", JoinType.INNER));
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            listJoins.add(root.join("amenities", JoinType.INNER));
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            listJoins.add(root.join("capacities", JoinType.INNER));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            listJoins.add(root.join("categories", JoinType.INNER));
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            listJoins.add(root.join("entertainments", JoinType.INNER));
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            listJoins.add(root.join("parkings", JoinType.INNER));
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            listJoins.add(root.join("prices", JoinType.INNER));
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            listJoins.add(root.join("purposes", JoinType.INNER));
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            listJoins.add(root.join("serviceTypes", JoinType.INNER));
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            listJoins.add(root.join("spaces", JoinType.INNER));
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            listJoins.add(root.join("specialties", JoinType.INNER));
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            listJoins.add(root.join("visitTimes", JoinType.INNER));
        }

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(predicate);

        int joinIndex = 0;
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getAmbiances()));
            joinIndex++;
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getAmenities()));
            joinIndex++;
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getCapacities()));
            joinIndex++;
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getCategories()));
            joinIndex++;
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getEntertainments()));
            joinIndex++;
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getParkings()));
            joinIndex++;
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getPrices()));
            joinIndex++;
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getPurposes()));
            joinIndex++;
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getServiceTypes()));
            joinIndex++;
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getSpaces()));
            joinIndex++;
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getSpecialties()));
            joinIndex++;
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            predicates.add(listJoins.get(joinIndex).get("id").in(request.getVisitTimes()));
            joinIndex++;
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Group by coffee shop ID for HAVING
        criteriaQuery.groupBy(root.get("id"));

        // HAVING clauses like countDistinct = list size
        List<Predicate> havingPredicates = new ArrayList<>();
        joinIndex = 0;
        if (request.getAmbiances() != null && !request.getAmbiances().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getAmbiances().size()));
            joinIndex++;
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getAmenities().size()));
            joinIndex++;
        }
        if (request.getCapacities() != null && !request.getCapacities().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getCapacities().size()));
            joinIndex++;
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getCategories().size()));
            joinIndex++;
        }
        if (request.getEntertainments() != null && !request.getEntertainments().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getEntertainments().size()));
            joinIndex++;
        }
        if (request.getParkings() != null && !request.getParkings().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getParkings().size()));
            joinIndex++;
        }
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getPrices().size()));
            joinIndex++;
        }
        if (request.getPurposes() != null && !request.getPurposes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getPurposes().size()));
            joinIndex++;
        }
        if (request.getServiceTypes() != null && !request.getServiceTypes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getServiceTypes().size()));
            joinIndex++;
        }
        if (request.getSpaces() != null && !request.getSpaces().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getSpaces().size()));
            joinIndex++;
        }
        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getSpecialties().size()));
            joinIndex++;
        }
        if (request.getVisitTimes() != null && !request.getVisitTimes().isEmpty()) {
            havingPredicates.add(criteriaBuilder.equal(criteriaBuilder.countDistinct(listJoins.get(joinIndex).get("id")), request.getVisitTimes().size()));
            joinIndex++;
        }

        if (!havingPredicates.isEmpty()) {
            criteriaQuery.having(havingPredicates.toArray(new Predicate[0]));
        }

        // Select distinct root entities
        criteriaQuery.select(root).distinct(true);

//        criteriaQuery.where(predicate);
        Sort sort = pageable.getSort();
        List<Order> orders = new ArrayList<>();

        for (Sort.Order sortOrder : sort) {
            if (sortOrder.isAscending()) {
                orders.add(criteriaBuilder.asc(root.get(sortOrder.getProperty())));
            } else {
                orders.add(criteriaBuilder.desc(root.get(sortOrder.getProperty())));
            }
        }

        criteriaQuery.orderBy(orders);

        // Apply sorting
        if (request.getSort() != null) {
            Join<CoffeeShop, Review> coffeeShopReviews = root.join("reviews",
                    JoinType.LEFT);
            criteriaQuery.groupBy(root.get("id"));

            if (CoffeeShopSort.HIGHEST_RATED.getValue().equals(request.getSort())) {
                // Handle NULL values with COALESCE for average rating
                Expression<Double> averageRating = criteriaBuilder.coalesce(
                        criteriaBuilder.avg(coffeeShopReviews.get("rating")),
                        0.0
                );
                criteriaQuery.orderBy(criteriaBuilder.desc(averageRating));
            } else if (CoffeeShopSort.MOST_REVIEW.getValue().equals(request.getSort())) {
                Expression<Long> reviewCount = criteriaBuilder.count(coffeeShopReviews.get("id"));
                criteriaQuery.orderBy(criteriaBuilder.desc(reviewCount));
            } else {
                // Default sorting if needed
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
            }
        }

        // Create the typed query and apply pagination
        TypedQuery<CoffeeShop> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Execute and return results
        return typedQuery.getResultList();
    }

    private Predicate buildPredicate(CoffeeShopSearchRequest request,
                                     CriteriaBuilder criteriaBuilder,
                                     Root<CoffeeShop> root, boolean isFromPrompt) {
        List<Predicate> predicates = new ArrayList<>();
        // Name filter
        if (request.getName() != null && !request.getName().isEmpty()) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + request.getName().toLowerCase() + "%"
            ));
        }

        if (BooleanUtils.isTrue(request.getIsOpening())) {
            // Get current time in minutes since midnight
            LocalTime now = LocalTime.now();
            int currentMinutes = now.getHour() * 60 + now.getMinute();

            // Create predicate to check if current time is between openHour and closeHour
            Predicate isOpen = criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get("openHour"),
                            currentMinutes),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("closeHour"),
                            currentMinutes)
            );
            predicates.add(isOpen);
        }

        // Address filters
        if (request.getProvince() != null && !request.getProvince().isEmpty()) {
            predicates.add(criteriaBuilder.equal(
                    root.get("address").get("province"),
                    request.getProvince()
            ));
        }

        if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
            predicates.add(criteriaBuilder.equal(
                    root.get("address").get("district"),
                    request.getDistrict()
            ));
        }

        if (request.getWard() != null && !request.getWard().isEmpty()) {
            predicates.add(criteriaBuilder.equal(
                    root.get("address").get("ward"),
                    request.getWard()
            ));
        }

        if (ObjectUtils.allNotNull(request.getLongitude(),
                request.getLatitude(),
                request.getDistance())) {
            Expression<Boolean> geoPredicate = criteriaBuilder.function(
                    "ST_DWithin",
                    Boolean.class,
                    root.get("address").get("location"),
                    criteriaBuilder.function(
                            "ST_MakePoint",
                            Object.class,
                            criteriaBuilder.literal(request.getLongitude()),
                            criteriaBuilder.literal(request.getLatitude())
                    ).as(Object.class),
                    criteriaBuilder.literal(request.getDistance() * 1000)
            );
            predicates.add(criteriaBuilder.isTrue(geoPredicate));
        }

        // Add list predicates
        if (isFromPrompt) {
            addListPredicate(predicates,
                    request.getAmbiances(),
                    root,
                    criteriaBuilder,
                    "ambiances");
            addListPredicate(predicates,
                    request.getAmenities(),
                    root,
                    criteriaBuilder,
                    "amenities");
            addListPredicate(predicates,
                    request.getCapacities(),
                    root,
                    criteriaBuilder,
                    "capacities");
            addListPredicate(predicates,
                    request.getCategories(),
                    root,
                    criteriaBuilder,
                    "categories");
            addListPredicate(predicates,
                    request.getEntertainments(),
                    root,
                    criteriaBuilder,
                    "entertainments");
            addListPredicate(predicates,
                    request.getParkings(),
                    root,
                    criteriaBuilder,
                    "parkings");
            addListPredicate(predicates,
                    request.getPrices(),
                    root,
                    criteriaBuilder,
                    "prices");
            addListPredicate(predicates,
                    request.getPurposes(),
                    root,
                    criteriaBuilder,
                    "purposes");
            addListPredicate(predicates,
                    request.getServiceTypes(),
                    root,
                    criteriaBuilder,
                    "serviceTypes");
            addListPredicate(predicates,
                    request.getSpaces(),
                    root,
                    criteriaBuilder,
                    "spaces");
            addListPredicate(predicates,
                    request.getSpecialties(),
                    root,
                    criteriaBuilder,
                    "specialties");
            addListPredicate(predicates,
                    request.getVisitTimes(),
                    root,
                    criteriaBuilder,
                    "visitTimes");
        }


        // Combine all predicates with AND
        return predicates.isEmpty()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    private void addListPredicate(List<Predicate> predicates,
                                  List<Long> values,
                                  Root<CoffeeShop> root,
                                  CriteriaBuilder criteriaBuilder,
                                  String fieldName) {
        if (values != null && !values.isEmpty()) {
            predicates.add(root.join(fieldName).get("id").in(values));
        }
    }
}
