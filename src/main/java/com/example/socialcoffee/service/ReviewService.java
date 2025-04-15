package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.dto.response.ReviewResponse;
import com.example.socialcoffee.dto.response.ReviewVM;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.*;
import com.example.socialcoffee.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final CoffeeShopRepository coffeeShopRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final ReviewReactionRepository reviewReactionRepository;

    @Transactional
    public ResponseEntity<ResponseMetaData> uploadReview(Long shopId,
                                                         Integer rating,
                                                         String content,
                                                         Boolean isAnonymous,
                                                         MultipartFile[] file,
                                                         Long parentId) {
        Long userId = SecurityUtil.getUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        User user = optionalUser.get();
        List<Image> images = imageService.save(file);
        Review review = new Review(rating,
                                   content,
                                   isAnonymous,
                                   images,
                                   user,
                                   coffeeShop);
        review = reviewRepository.save(review);
        coffeeShop.addReview(review);
        coffeeShopRepository.save(coffeeShop);
        user.addReview(review);
        userRepository.save(user);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> deleteReview(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Review review = optionalReview.get();
        review.setStatus(Status.INACTIVE.getValue());
        reviewRepository.save(review);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> getReviewByShopId(Long shopId,
                                                              PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1,
                                           pageDtoIn.getSize(),
                                           Sort.unsorted());
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Page<Review> reviews = reviewRepository.findAllByCoffeeShopAndStatus(coffeeShop,
                                                                             Status.ACTIVE.getValue(),
                                                                             pageable);
        List<ReviewVM> reviewVMS = reviews.getContent()
                .stream()
                .map(r -> new ReviewVM(r, groupedReactions.getOrDefault(r.getId(), null)))
                .toList();
        final long totalElements = reviews.getTotalElements();
        PageDtoOut<ReviewVM> pageDtoOut = PageDtoOut.from(pageable.getPageNumber(),
                                                          pageable.getPageSize(),
                                                          totalElements,
                                                          reviewVMS);
        List<Object[]> reviewSummary = reviewRepository.getReviewSummary(coffeeShop);

        ReviewResponse reviewResponse = new ReviewResponse(pageDtoOut,
                                                           reviewSummary,
                                                           totalElements);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                      reviewResponse));
    }

    private Map<Long, Map<String, Long>> groupedReactions(List<Long> reviewIds) {
        List<ReviewReaction> reviewReactions = reviewReactionRepository.findByReviewIdIn(reviewIds);
        return reviewReactions.stream()
                .collect(Collectors.groupingBy(
                        rr -> rr.getId().getReviewId(),
                        Collectors.toMap(
                                ReviewReaction::getType,
                                rr -> 1L,
                                Long::sum
                        )
                ));
    }
    public ResponseEntity<ResponseMetaData> editReview(Long shopId, Long reviewId, EditReviewRequest editReviewRequest) {
        Review review = reviewRepository.findByIdAndCoffeeShopIdAndStatus(reviewId, shopId, Status.ACTIVE.getValue());
        if (Objects.isNull(review)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        List<Image> newImages = new ArrayList<>();
        if (!CollectionUtils.isEmpty(editReviewRequest.getEditReviewImages())) {
            List<Long> editReviewImageIds = editReviewRequest.getEditReviewImages()
                    .stream()
                    .filter(EditReviewRequest.EditReviewImage::getIsDeleteResource)
                    .map(EditReviewRequest.EditReviewImage::getId)
                    .toList();
            List<Image> deletedImages = imageRepository.findAllById(editReviewImageIds);
            deletedImages.forEach(image -> image.setStatus(Status.INACTIVE.getValue()));
            imageRepository.saveAll(deletedImages);
        }
        if (Objects.nonNull(editReviewRequest.getResources()) && editReviewRequest.getResources().length > 0) {
            newImages = imageService.save(editReviewRequest.getResources());
        }

        review.setComment(editReviewRequest.getContent());
        review.setRating(editReviewRequest.getRating());
        review.addImages(newImages);
        reviewRepository.save(review);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> react(Long userId, Long reviewId, String reaction) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        ReviewReaction.ReviewReactionId reviewReactionId = new ReviewReaction.ReviewReactionId(reviewId, userId);
        Optional<ReviewReaction> optionalReviewReaction = reviewReactionRepository.findById(reviewReactionId);
        if (optionalReviewReaction.isEmpty()) {
            ReviewReaction reviewReaction = new ReviewReaction(reviewReactionId, reaction);
            reviewReactionRepository.save(reviewReaction);
        } else {
            ReviewReaction reviewReaction = optionalReviewReaction.get();
            reviewReaction.setType(reaction);
        }
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> getReviews(PageDtoIn pageDtoIn) {
        PageRequest pageRequest = PageRequest.of(pageDtoIn.getPage(),
                                                 pageDtoIn.getSize(),
                                                 Sort.by(Sort.Direction.DESC,
                                                         "createdAt"));
        final Page<Review> reviews = reviewRepository.findAll(pageRequest);
        List<ReviewVM> reviewVMS = reviews.getContent().stream().map(ReviewVM::new).toList();
        PageDtoOut<ReviewVM> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                                                          pageDtoIn.getSize(),
                                                          reviews.getTotalElements(),
                                                          reviewVMS);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                                                             pageDtoOut));
    }
}
