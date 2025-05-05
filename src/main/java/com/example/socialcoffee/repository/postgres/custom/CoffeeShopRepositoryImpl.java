package com.example.socialcoffee.repository.postgres.custom;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Review;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.enums.CoffeeShopSort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                                              Integer size) {
        Pageable pageable = PageRequest.of(page,
                                           size);

        Long totalCount = getTotalCount(request);

        // Get the paginated results
        List<CoffeeShop> coffeeShops = getPagedResults(request,
                                                       pageable);

        // Return PageImpl with results, pageable, and total count
        return new PageImpl<>(coffeeShops,
                              pageable,
                              totalCount);
    }

    private Long getTotalCount(CoffeeShopSearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<CoffeeShop> root = countQuery.from(CoffeeShop.class);

        // Apply the same predicates as the main query
        Predicate predicate = buildPredicate(request,
                                             criteriaBuilder,
                                             root);

        // Count distinct IDs to handle joins properly
        countQuery.select(criteriaBuilder.countDistinct(root.get("id")));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<CoffeeShop> getPagedResults(CoffeeShopSearchRequest request,
                                             Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CoffeeShop> criteriaQuery = criteriaBuilder.createQuery(CoffeeShop.class);
        Root<CoffeeShop> root = criteriaQuery.from(CoffeeShop.class);

        // Apply predicates
        Predicate predicate = buildPredicate(request,
                                             criteriaBuilder,
                                             root);
        criteriaQuery.where(predicate);

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
                                     Root<CoffeeShop> root) {
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

        // Combine all predicates with AND
        return predicates.isEmpty()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
