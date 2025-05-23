package com.example.socialcoffee.repository.postgres.specification;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Review;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.enums.CoffeeShopSort;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CoffeeShopSpecification {

    public static Specification<CoffeeShop> searchCoffeeShops(CoffeeShopSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (request.getName() != null && !request.getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getProvince() != null && !request.getProvince().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("province"),
                                request.getProvince()));
            }

            if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("district"),
                                request.getDistrict()));
            }

            if (request.getWard() != null && !request.getWard().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("ward"),
                                request.getWard()));
            }

            predicate = addListPredicate(request.getAmbiances(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "ambiances");
            predicate = addListPredicate(request.getAmenities(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "amenities");
            predicate = addListPredicate(request.getCapacities(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "capacities");
            predicate = addListPredicate(request.getCategories(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "categories");
            predicate = addListPredicate(request.getEntertainments(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "entertainments");
            predicate = addListPredicate(request.getParkings(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "parkings");
            predicate = addListPredicate(request.getPrices(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "prices");
            predicate = addListPredicate(request.getPurposes(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "purposes");
            predicate = addListPredicate(request.getServiceTypes(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "serviceTypes");
            predicate = addListPredicate(request.getSpaces(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "spaces");
            predicate = addListPredicate(request.getSpecialties(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "specialties");
            predicate = addListPredicate(request.getVisitTimes(),
                    root,
                    criteriaBuilder,
                    predicate,
                    "visitTimes");

            if (request.getSort() != null) {
                Join<CoffeeShop, Review> coffeeShopReviews = root.join("reviews", JoinType.LEFT);
                query.distinct(true);
                query.groupBy(root.get("id"));
                if (CoffeeShopSort.HIGHEST_RATED.equals(request.getSort())) {
                    Expression<Double> averageRating = criteriaBuilder.coalesce(
                            criteriaBuilder.avg(coffeeShopReviews.get("rating")),
                            0.0
                    );
                    query.orderBy(criteriaBuilder.desc(averageRating));
                } else if (CoffeeShopSort.MOST_REVIEW.equals(request.getSort())) {
                    query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(coffeeShopReviews.get("id"))));
                }
            }
            return predicate;
        };
    }

    private static Predicate addListPredicate(List<Long> values,
                                              Root<CoffeeShop> root,
                                              CriteriaBuilder cb,
                                              Predicate predicate,
                                              String fieldName) {
        if (values != null && !values.isEmpty()) {
            return cb.and(predicate,
                    root.join(fieldName).get("id").in(values));
        }
        return predicate;
    }
}
