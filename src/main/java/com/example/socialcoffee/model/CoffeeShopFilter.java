package com.example.socialcoffee.model;

import com.example.socialcoffee.domain.postgres.feature.*;
import com.example.socialcoffee.dto.common.DistanceDTO;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoffeeShopFilter {
    private String province;

    private String district;

    private String ward;

    @JsonIgnore
    private String isOpening = "true/false";

    private List<DistanceDTO> distances;

    private List<Ambiance> ambiances;

    private List<Amenity> amenities;

    private List<Capacity> capacities;

    private List<Entertainment> entertainments;

    private List<Parking> parkings;

    private List<Price> prices;

    private List<Purpose> purposes;

    private List<ServiceType> serviceTypes;

    private List<Space> spaces;

    private List<Specialty> specialties;

    private List<VisitTime> visitTimes;

    @JsonProperty(value = "isSuggested")
    private Boolean isSuggested;

    public CoffeeShopSearchRequest toSearchRequest(List<Ambiance> ambiances,
                                                   List<Amenity> amenities,
                                                   List<Capacity> capacities,
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
                .entertainments(getEntertainmentValues(entertainments))
                .parkings(getParkingValues(parkings))
                .purposes(getPurposeValues(purposes))
                .serviceTypes(getServiceTypeValues(serviceTypes))
                .spaces(getSpaceValues(spaces))
                .specialties(getSpecialtyValues(specialties))
                .visitTimes(getVisitTimeValues(visitTimes))
                .province(this.province)
                .district(this.district)
                .ward(this.ward)
                .build();
    }

    @JsonIgnore
    public List<Long> getAmbianceValues(List<Ambiance> ambiancesEntity) {
        return extractFeatureIds(ambiances);
    }

    @JsonIgnore
    public List<Long> getAmenityValues(List<Amenity> amenitiesEntity) {
        return extractFeatureIds(amenities);
    }

    @JsonIgnore
    public List<Long> getCapacityValues(List<Capacity> capacitiesEntity) {
        return extractFeatureIds(capacities);
    }

    @JsonIgnore
    public List<Long> getEntertainmentValues(List<Entertainment> entertainmentsEntity) {
        return extractFeatureIds(entertainments);
    }

    @JsonIgnore
    public List<Long> getParkingValues(List<Parking> parkingsEntity) {
        return extractFeatureIds(parkings);
    }

    @JsonIgnore
    public List<Long> getPurposeValues(List<Purpose> purposesEntity) {
        return extractFeatureIds(purposes);
    }

    @JsonIgnore
    public List<Long> getServiceTypeValues(List<ServiceType> serviceTypesEntity) {
        return extractFeatureIds(serviceTypes);
    }

    @JsonIgnore
    public List<Long> getSpaceValues(List<Space> spacesEntity) {
        return extractFeatureIds(spaces);
    }

    @JsonIgnore
    public List<Long> getSpecialtyValues(List<Specialty> specialtiesEntity) {
        return extractFeatureIds(specialties);
    }

    @JsonIgnore
    public List<Long> getVisitTimeValues(List<VisitTime> visitTimesEntity) {
        return extractFeatureIds(visitTimes);
    }

    private <T extends Feature> List<Long> extractFeatureIds(List<T> filterList) {
        if (filterList == null) return Collections.emptyList();
        return filterList.stream()
                .map(f -> f.getId())
                .collect(Collectors.toList());
    }
}
