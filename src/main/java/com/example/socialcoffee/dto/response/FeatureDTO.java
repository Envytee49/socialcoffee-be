package com.example.socialcoffee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO {
    protected Long id;

    protected String value;

    protected boolean isSet;

    protected boolean isInSearchPrompt;

    public static class AmbianceDto extends FeatureDTO {

    }

    public static class AmenityDto extends FeatureDTO {

    }

    public static class CapacityDto extends FeatureDTO {

    }

    public static class CategoryDto extends FeatureDTO {

    }

    public static class DressCodeDto extends FeatureDTO {

    }

    public static class EntertainmentDto extends FeatureDTO {
    }

    public static class ParkingDto extends FeatureDTO {

    }

    public static class PriceDto extends FeatureDTO {

    }

    public static class PurposeDto extends FeatureDTO {

    }

    public static class ServiceTypeDto extends FeatureDTO {
    }

    public static class SpaceDto extends FeatureDTO {

    }

    public static class SpecialtyDto extends FeatureDTO {

    }

    public static class VisitTimeDto extends FeatureDTO {

    }
}
