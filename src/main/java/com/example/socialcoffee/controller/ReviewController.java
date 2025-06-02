package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.RepoService;
import com.example.socialcoffee.service.ReviewService;
import com.example.socialcoffee.service.ValidationService;
import com.example.socialcoffee.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Valid
@Validated
public class ReviewController extends BaseController {
    private final ReviewService reviewService;

    private final ValidationService validationService;

    private final RepoService repoService;

    @PutMapping("/reviews/{reviewID}/react")
    public ResponseEntity<ResponseMetaData> react(@PathVariable("reviewID") Long reviewId,
                                                  String reaction) {
        List<MetaDTO> metaDTOS = validationService.validateReviewReact(reaction);
        if (!CollectionUtils.isEmpty(metaDTOS)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOS));
        }
        return reviewService.react(reviewId,
                reaction);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ResponseMetaData> getReviews(PageDtoIn pageDtoIn, @RequestParam(defaultValue = "highest_score") String sortBy) {
        return reviewService.getReviews(pageDtoIn, sortBy);
    }

    @GetMapping("/reviews/reaction")
    public ResponseEntity<ResponseMetaData> getReviewReaction(@RequestParam(value = "type") String type,
                                                              @RequestParam(value = "reviewId") Long reviewId) {
        return reviewService.getReviewReaction(SecurityUtil.getUserId(),
                type,
                reviewId);
    }

    @DeleteMapping("/reviews/{review_id}")
    public ResponseEntity<ResponseMetaData> deleteReview(@PathVariable("review_id") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @PostMapping("/migrate/reviews")
    public ResponseEntity<ResponseMetaData> migrateReviews() {
        repoService.migrateReviews();
        return ResponseEntity.ok().build();
    }


}
