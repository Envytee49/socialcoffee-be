package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.service.ReviewService;
import com.example.socialcoffee.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ReviewController {
    private final ReviewService reviewService;
    private final ValidationService validationService;

    @PostMapping("coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> uploadReview(@PathVariable("shop_id") Long shopId,
                                                         @RequestPart(value = "rating") String rating,
                                                         @RequestPart(value = "content", required = false) String content,
                                                         @RequestPart(value = "is_annonymous", required = false) String isAnonymous,
                                                         @RequestPart(value = "review_id", required = false) String parentId,
                                                         @RequestPart(value = "resource", required = false) MultipartFile[] file) {
        content = StringUtils.trimToEmpty(content);
        List<MetaDTO> metaDTOList = validationService.validationCommentPost(content, file, Boolean.TRUE);
        if (!CollectionUtils.isEmpty(metaDTOList)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(metaDTOList, null));
        }
        return reviewService.uploadReview(shopId, Integer.parseInt(rating), content, Boolean.parseBoolean(isAnonymous), file, NumberUtils.toLong(parentId));
    }
    @PutMapping("coffee-shops/{shop_id}/review/{review_id}")
    public ResponseEntity<ResponseMetaData> editReview(@PathVariable("shop_id") Long shopId,
                                                       @PathVariable("review_id") Long reviewId,
                                                       @ModelAttribute @RequestPart EditReviewRequest editReviewRequest) {
        return reviewService.editReview(shopId, reviewId, editReviewRequest);
    }
    @GetMapping("coffee-shops/{shop_id}/review")
    public ResponseEntity<ResponseMetaData> getReview(@PathVariable("shop_id") Long shopId,
                                                      PageDtoIn pageDtoIn) {
        return reviewService.getReviewByShopId(shopId, pageDtoIn);
    }
    @GetMapping("/reviews")
    public ResponseEntity<ResponseMetaData> getReviews(PageDtoIn pageDtoIn) {
        return reviewService.getReviews(pageDtoIn);
    }
    @DeleteMapping("coffee-shops/{shop_id}/review/{review_id}")
    public ResponseEntity<ResponseMetaData> deleteReview(@PathVariable("review_id") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }


}
