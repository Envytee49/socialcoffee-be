package com.example.socialcoffee.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@UtilityClass
public class NumberUtil {
    public static Double roundToTwoDecimals(Double value) {
        if(Objects.isNull(value)) return null;
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static Double roundToTwoDecimals(Double value, Double defaultValue) {
        if(Objects.isNull(value)) return defaultValue;
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
