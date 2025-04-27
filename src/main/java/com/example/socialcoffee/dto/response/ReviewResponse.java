package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.utils.NumberUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class ReviewResponse {
    private PageDtoOut<ReviewVM> data;
    private Pair<Integer, Double> oneStarSummary;
    private Pair<Integer, Double> twoStarSummary;
    private Pair<Integer, Double> threeStarSummary;
    private Pair<Integer, Double> fourStarSummary;
    private Pair<Integer, Double> fiveStarSummary;
    private Double overallRating;
    public ReviewResponse(PageDtoOut<ReviewVM> pageDtoOut,
                          List<Object[]> reviewSummary,
                          Long totalReviews) {
        this.data = pageDtoOut;
        if(!CollectionUtils.isEmpty(reviewSummary)) {
            this.oneStarSummary = toSummary(reviewSummary,
                                            1,
                                            totalReviews);
            this.twoStarSummary = toSummary(reviewSummary,
                                            2,
                                            totalReviews);
            this.threeStarSummary = toSummary(reviewSummary,
                                              3,
                                              totalReviews);
            this.fourStarSummary = toSummary(reviewSummary,
                                             4,
                                             totalReviews);
            this.fiveStarSummary = toSummary(reviewSummary,
                                             5,
                                             totalReviews);
            this.overallRating = 1.0 * reviewSummary.stream()
                    .map(r -> ((Number) r[0]).intValue() * ((Number) r[1]).intValue()).reduce(0, Integer::sum) / totalReviews;
            this.overallRating = NumberUtil.roundToTwoDecimals(this.overallRating,
                                                               0.0);
        }
    }

    private Pair<Integer, Double> toSummary(List<Object[]> summaryList, int star,
                                            final Long totalReviews) {
        return summaryList.stream()
                .filter(r -> ((Number) r[0]).intValue() == star)
                .findFirst()
                .map(r -> Pair.of(
                        ((Number) r[0]).intValue(),// count
                        NumberUtil.roundToTwoDecimals(((Number) r[1]).longValue() * 100.0 / totalReviews, 0.0)// percent
                ))
                .orElse(Pair.of(star, 0.0));
    }

}
