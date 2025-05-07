package com.example.socialcoffee.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StringAppUtils {
    public static boolean isEmpty(String str) {
        return !org.springframework.util.StringUtils.hasLength(str);
    }

    public static String getJson(String json) {
        int startIndex = json.indexOf('{');
        int endIndex = json.lastIndexOf('}');

        return json.substring(startIndex,
                              endIndex + 1);
    }

    public static List<String> formatedListPrices(List<String> prices) {
        if (CollectionUtils.isEmpty(prices)) return null;
        List<String> formatted = new ArrayList<>();
        for (String range : prices) {
            String fixed = range.replaceAll("\\s*-\\s*", " - ");
            formatted.add(fixed);
        }
        return formatted;
    }
}
