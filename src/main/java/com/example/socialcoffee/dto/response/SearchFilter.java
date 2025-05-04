package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.feature.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchFilter {
    private List<DistanceDTO> distances;
    private List<SortDTO> sorts;
    private List<Ambiance> ambiances;
    private List<Amenity> amenities;
    private List<Capacity> capacities;
    private List<Category> categories;
    private List<Entertainment> entertainments;
    private List<Parking> parkings;
    private List<Price> prices;
    private List<Purpose> purposes;
    private List<ServiceType> serviceTypes;
    private List<Space> spaces;
    private List<Specialty> specialties;
    private List<VisitTime> visitTimes;
    @Setter
    @Getter
    @AllArgsConstructor
    public static class DistanceDTO {
        private Long id;
        private Integer value;
    }
    @Setter
    @Getter
    @AllArgsConstructor
    public static class SortDTO {
        private Long id;
        private String value;
    }
}
