package com.example.socialcoffee.enums;

public enum FeatureType {
    AMBIANCE("ambiance"),
    AMENITY("amenity"),
    CAPACITY("capacity"),
    CATEGORY("category"),
    DRESS_CODE("dressCode"),
    ENTERTAINMENT("entertainment"),
    PARKING("parking"),
    PRICE("price"),
    SERVICE_TYPE("serviceType"),
    SPECIALTY("specialty"),
    SPACE("space"),
    VISIT_TIME("visitTime");

    private final String key;

    FeatureType(String key) {
        this.key = key;
    }

    public static FeatureType fromKey(String key) {
        for (FeatureType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid feature type: " + key);
    }

    public String getKey() {
        return key;
    }
}


