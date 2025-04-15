package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.*;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.dto.response.ReviewVM;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService extends BaseService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final CoffeeShopRepository coffeeShopRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final ReviewReactionRepository reviewReactionRepository;

    public ResponseEntity<ResponseMetaData> uploadReview(Long shopId, String privacy, Integer rating,
                                                         String content, Boolean isAnonymous, MultipartFile[] file, Long parentId) {
        User user = getCurrentUser();
        if (Objects.isNull(user)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        List<Image> images = imageService.save(file);
        Review review = new Review(rating, privacy, content, isAnonymous, images);
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

    public ResponseEntity<ResponseMetaData> getReview(Long shopId, PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getSize(),
                Sort.unsorted());
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Page<Review> reviews = reviewRepository.findAllByCoffeeShop(coffeeShop, pageable);
        List<Long> reviewIds = reviews.getContent().stream().map(Review::getId).toList();
        Map<Long, Map<String, Long>> groupedReactions = groupedReactions(reviewIds);
        List<ReviewVM> reviewVMS = reviews.getContent().stream()
                .filter(r -> Status.ACTIVE.getValue().equals(r.getStatus()))
                .map(r -> new ReviewVM(r, groupedReactions.getOrDefault(r.getId(), null))).toList();
        PageDtoOut<ReviewVM> pageDtoOut = PageDtoOut.from(pageable.getPageNumber(),
                pageable.getPageSize(),
                reviews.getTotalElements(),
                reviewVMS);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), pageDtoOut));
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

        review.setTitle(editReviewRequest.getTitle());
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
}
