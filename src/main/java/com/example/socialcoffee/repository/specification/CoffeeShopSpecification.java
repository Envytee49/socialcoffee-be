package com.example.socialcoffee.repository.specification;

import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.model.CoffeeShop;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.List;

public class CoffeeShopSpecification {

    public static Specification<CoffeeShop> searchCoffeeShops(CoffeeShopSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (request.getName() != null && !request.getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getProvince() != null && !request.getProvince().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("province"), request.getProvince()));
            }

            if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("district"), request.getDistrict()));
            }

            if (request.getWard() != null && !request.getWard().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("address").get("ward"), request.getWard()));
            }

            // Handling List<Long> parameters
            addListPredicate(request.getAmbiances(), root, criteriaBuilder, predicate, "ambiances");
            addListPredicate(request.getAmenities(), root, criteriaBuilder, predicate, "amenities");
            addListPredicate(request.getCapacities(), root, criteriaBuilder, predicate, "capacities");
            addListPredicate(request.getCategories(), root, criteriaBuilder, predicate, "categories");
            addListPredicate(request.getEntertainments(), root, criteriaBuilder, predicate, "entertainments");
            addListPredicate(request.getParkings(), root, criteriaBuilder, predicate, "parkings");
            addListPredicate(request.getPrices(), root, criteriaBuilder, predicate, "prices");
            addListPredicate(request.getPurposes(), root, criteriaBuilder, predicate, "purposes");
            addListPredicate(request.getServiceTypes(), root, criteriaBuilder, predicate, "serviceTypes");
            addListPredicate(request.getSpaces(), root, criteriaBuilder, predicate, "spaces");
            addListPredicate(request.getSpecialties(), root, criteriaBuilder, predicate, "specialties");
            addListPredicate(request.getVisitTimes(), root, criteriaBuilder, predicate, "visitTimes");

            return predicate;
        };
    }

    private static void addListPredicate(List<Long> values, Root<CoffeeShop> root, CriteriaBuilder cb, Predicate predicate, String fieldName) {
        if (values != null && !values.isEmpty()) {
            predicate.getExpressions().add(root.join(fieldName).get("id").in(values));
        }
    }
}
