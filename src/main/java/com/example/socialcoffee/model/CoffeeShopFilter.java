package com.example.socialcoffee.model;

import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.enums.Distance;
import com.example.socialcoffee.utils.StringAppUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Slf4j
public class CoffeeShopFilter {
    private String isOpening = "true/false";

    private List<String> distances;

    private List<String> ambiances;

    private List<String> amenities;

    private List<String> capacities;

    private List<String> categories;

    private List<String> entertainments;

    private List<String> parkings;

    private List<String> prices;

    private List<String> purposes;

    private List<String> serviceTypes;

    private List<String> spaces;

    private List<String> specialties;

    private List<String> visitTimes;

    public List<Integer> getDistanceValues() {
        List<Integer> values = new ArrayList<>();
        for (final String distance : distances) {
            try {
                final Distance value = Distance.valueOf(distance);
                values.add(value.getValue());
            } catch (Exception e) {
                log.warn("Warning: ",
                        e);
            }
        }
        return values;
    }

    public CoffeeShopSearchRequest toSearchRequest(List<Ambiance> ambiances,
                                                   List<Amenity> amenities,
                                                   List<Capacity> capacities,
                                                   List<Category> categories,
                                                   List<Entertainment> entertainments,
                                                   List<Parking> parkings,
                                                   List<Price> prices,
                                                   List<Purpose> purposes,
                                                   List<ServiceType> serviceTypes,
                                                   List<Space> spaces,
                                                   List<Specialty> specialties,
                                                   List<VisitTime> visitTimes) {
        return CoffeeShopSearchRequest.builder()
                .isOpening(BooleanUtils.toBoolean(this.isOpening))
                .ambiances(getAmbianceValues(ambiances))
                .amenities(getAmenityValues(amenities))
                .capacities(getCapacityValues(capacities))
                .categories(getCategoryValues(categories))
                .entertainments(getEntertainmentValues(entertainments))
                .parkings(getParkingValues(parkings))
                .prices(getPriceValues(prices))
                .purposes(getPurposeValues(purposes))
                .serviceTypes(getServiceTypeValues(serviceTypes))
                .spaces(getSpaceValues(spaces))
                .specialties(getSpecialtyValues(specialties))
                .visitTimes(getVisitTimeValues(visitTimes))
                .build();
    }

    public List<Long> getAmbianceValues(List<Ambiance> ambiancesEntity) {
        return extractFeatureIds(ambiances,
                ambiancesEntity);
    }

    public List<Long> getAmenityValues(List<Amenity> amenitiesEntity) {
        return extractFeatureIds(amenities,
                amenitiesEntity);
    }

    public List<Long> getCapacityValues(List<Capacity> capacitiesEntity) {
        return extractFeatureIds(capacities,
                capacitiesEntity);
    }

    public List<Long> getCategoryValues(List<Category> categoriesEntity) {
        return extractFeatureIds(categories,
                categoriesEntity);
    }

    public List<Long> getEntertainmentValues(List<Entertainment> entertainmentsEntity) {
        return extractFeatureIds(entertainments,
                entertainmentsEntity);
    }

    public List<Long> getParkingValues(List<Parking> parkingsEntity) {
        return extractFeatureIds(parkings,
                parkingsEntity);
    }

    public List<Long> getPriceValues(List<Price> pricesEntity) {
        return extractFeatureIds(StringAppUtils.formatedListPrices(prices),
                pricesEntity);
    }

    public List<Long> getPurposeValues(List<Purpose> purposesEntity) {
        return extractFeatureIds(purposes,
                purposesEntity);
    }

    public List<Long> getServiceTypeValues(List<ServiceType> serviceTypesEntity) {
        return extractFeatureIds(serviceTypes,
                serviceTypesEntity);
    }

    public List<Long> getSpaceValues(List<Space> spacesEntity) {
        return extractFeatureIds(spaces,
                spacesEntity);
    }

    public List<Long> getSpecialtyValues(List<Specialty> specialtiesEntity) {
        return extractFeatureIds(specialties,
                specialtiesEntity);
    }

    public List<Long> getVisitTimeValues(List<VisitTime> visitTimesEntity) {
        return extractFeatureIds(visitTimes,
                visitTimesEntity);
    }

    private <T extends Feature> List<Long> extractFeatureIds(List<String> filterList,
                                                             List<T> entityList) {
        if (filterList == null || entityList == null) return Collections.emptyList();
        Map<String, Long> valueMap = entityList.stream()
                .collect(Collectors.toMap(Feature::getValue,
                        Feature::getId));
        return filterList.stream()
                .map(valueMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
