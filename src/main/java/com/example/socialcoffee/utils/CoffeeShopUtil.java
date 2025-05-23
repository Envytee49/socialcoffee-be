package com.example.socialcoffee.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class CoffeeShopUtil {
    public static Map<Long, Pair<Double, Long>> toRatingMap(List<Object[]> reviewSummaries) {
        Map<Long, Pair<Double, Long>> ratingAndReviewCountMap = new HashMap<>();

        for (Object[] reviewSummary : reviewSummaries) {
            if (reviewSummary == null || reviewSummary.length < 2) continue;

            Number idObj = (Number) reviewSummary[0];
            Double rating = Objects.nonNull(reviewSummary[1]) ? ((Number) reviewSummary[1]).doubleValue() : 0.0;
            Long reviewCounts = Objects.nonNull(reviewSummary[2]) ? (Long) reviewSummary[2] : 0;
            if (Objects.nonNull(idObj)) {
                ratingAndReviewCountMap.put(idObj.longValue(),
                        Pair.of(rating, reviewCounts));
            }
        }

        return ratingAndReviewCountMap;
    }
}
