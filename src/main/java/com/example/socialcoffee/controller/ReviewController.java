package com.example.socialcoffee.controller;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.neo4j.relationship.Review;
import com.example.socialcoffee.service.RepoService;
import com.example.socialcoffee.service.ReviewService;
import com.example.socialcoffee.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Valid
@Validated
public class ReviewController extends BaseController {
    private final ReviewService reviewService;
    private final ValidationService validationService;

    private final RepoService repoService;

    @PostMapping("/coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> uploadReview(@PathVariable("shop_id") Long shopId,
                                                         @RequestPart(value = "rating") String rating,
                                                         @RequestPart(value = "content", required = false) String content,
                                                         @RequestPart(value = "is_annonymous", required = false) String isAnonymous,
                                                         @RequestPart(value = "review_id", required = false) String parentId,
                                                         @RequestPart(value = "resource", required = false) MultipartFile[] file) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        content = StringUtils.trimToEmpty(content);
        List<MetaDTO> metaDTOList = validationService.validationCommentPost(content,
                                                                            file,
                                                                            Boolean.TRUE);
        if (!CollectionUtils.isEmpty(metaDTOList)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOList,
                                                                         null));
        }
        return reviewService.uploadReview(user,
                                          shopId,
                                          Integer.parseInt(rating),
                                          content,
                                          file,
                                          NumberUtils.toLong(parentId));
    }

    @PutMapping("/reviews/{reviewID}/react")
    public ResponseEntity<ResponseMetaData> react(@PathVariable("reviewID") Long reviewId,
                                                  String reaction) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            return ResponseEntity.status(401).build();
        List<MetaDTO> metaDTOS = validationService.validateReviewReact(reaction);
        if (!CollectionUtils.isEmpty(metaDTOS)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOS));
        }
        return reviewService.react(user,
                                   reviewId,
                                   reaction);
    }

    @PutMapping("/coffee-shops/{shop_id}/review/{review_id}")
    public ResponseEntity<ResponseMetaData> editReview(@PathVariable("shop_id") Long shopId,
                                                       @PathVariable("review_id") Long reviewId,
                                                       @ModelAttribute @RequestPart EditReviewRequest editReviewRequest) {
        return reviewService.editReview(shopId,
                                        reviewId,
                                        editReviewRequest);
    }

    @GetMapping("/coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> getReviewByShopId(@PathVariable("shop_id") Long shopId,
                                                              PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) return ResponseEntity.status(401).build();
        return reviewService.getReviewByShopId(user,
                                               shopId,
                                               pageDtoIn);
    }

    @GetMapping("/users/{displayName}/reviews")
    public ResponseEntity<ResponseMetaData> getReviewByUserId(@PathVariable(value = "displayName") String displayName,
                                                              PageDtoIn pageDtoIn) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) return ResponseEntity.status(401).build();
        String destinationUser = Objects.isNull(displayName) ? user.getDisplayName() : displayName;
        return reviewService.getReviewByUserId(user,
                                               destinationUser,
                                               pageDtoIn);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ResponseMetaData> getReviews(PageDtoIn pageDtoIn, @RequestParam(defaultValue = "highest_score") String sortBy) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) return ResponseEntity.status(401).build();
        return reviewService.getReviews(user,
                                        pageDtoIn, sortBy);
    }

    @GetMapping("/reviews/reaction")
    public ResponseEntity<ResponseMetaData> getReviewReaction(@RequestParam(value = "type") String type,
                                                              @RequestParam(value = "reviewId") Long reviewId) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) return ResponseEntity.status(401).build();
        return reviewService.getReviewReaction(user.getId(),
                                               type,
                                               reviewId);
    }

    @DeleteMapping("/coffee-shops/{shop_id}/review/{review_id}")
    public ResponseEntity<ResponseMetaData> deleteReview(@PathVariable("review_id") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @PostMapping("/migrate/reviews")
    public ResponseEntity<ResponseMetaData> migrateReviews(@RequestBody List<Review> reviews) {
        repoService.migrateReviews();
        return ResponseEntity.ok().build();
    }


}
