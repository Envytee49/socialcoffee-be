package com.example.socialcoffee.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@UtilityClass
public class ObjectUtil {
    public static <T> List<T> getPageResult(List<T> list, Integer page, Integer size) {
        // Check for valid page and size values
        if (list == null || page == null || size == null || size <= 0 || page < 0) {
            throw new IllegalArgumentException("Invalid page or size value");
        }

        // Calculate the start and end indices for the sublist
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, list.size());

        // If the start index is greater than the list size, return an empty list
        if (startIndex >= list.size()) {
            return List.of();
        }

        // Return the sublist
        return list.subList(startIndex, endIndex);
    }

    public static String objectToString(ObjectMapper objectMapper, Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T stringToObject(ObjectMapper objectMapper, String string, Class<T> valueType) {
        try {
            if (StringUtils.isBlank(string)) return null;
            return objectMapper.readValue(string, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
