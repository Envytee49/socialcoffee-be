package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.postgres.*;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.EditReviewRequest;
import com.example.socialcoffee.dto.response.*;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.ReviewVote;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.UserReaction;
import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.repository.postgres.*;
import com.example.socialcoffee.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
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

    private final UserFollowRepository userFollowRepository;

    private final RepoService repoService;

    private final Neo4jClient neo4jClient;

    private final CacheableService cacheableService;

    @Transactional
    public ResponseEntity<ResponseMetaData> uploadReview(User user,
                                                         Long shopId,
                                                         Integer rating,
                                                         String content,
                                                         MultipartFile[] file,
                                                         Long parentId) {
        if (Objects.isNull(user)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        List<Image> images = imageService.save(file);
        Review review = new Review(rating,
                                   content,
                                   images,
                                   user,
                                   coffeeShop);
        review = reviewRepository.save(review);
        final NUser nUserById = repoService.findNUserById(user.getId());
        nUserById.addReview(neo4jClient, coffeeShop.getId(), review.getId(), rating);
        cacheableService.clearAllWhenReview();
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
        final Long userId = review.getUser().getId();
        final NUser nUserById = repoService.findNUserById(userId);
        nUserById.removeReview(neo4jClient, review.getCoffeeShop().getId(), reviewId);
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
        List<Long> reviewIds = reviews.getContent().stream().map(Review::getId).toList();
        Map<Long, UserReaction> groupedReactions = groupedReactions(reviewIds);

        List<ReviewVM> reviewVMS = reviews.getContent()
                .stream()
                .map(r -> new ReviewVM(SecurityUtil.getUserId(),
                        r,
                        groupedReactions.getOrDefault(r.getId(),
                                null)))
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

    private Map<Long, UserReaction> groupedReactions(List<Long> reviewIds) {
        List<ReviewReaction> reviewReactions = reviewReactionRepository.findByReviewIdIn(reviewIds);
        return reviewReactions.stream()
                .collect(Collectors.groupingBy(
                        rr -> rr.getId().getReviewId(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                reactionsForReview -> {
                                    UserReaction ur = new UserReaction();

                                    // Count total reactions
                                    long upvote = 0;
                                    long downvote = 0;

                                    for (ReviewReaction r : reactionsForReview) {
                                        if (ReviewVote.UPVOTE.getValue().equalsIgnoreCase(r.getType())) {
                                            upvote++;
                                        } else if (ReviewVote.DOWNVOTE.getValue().equalsIgnoreCase(r.getType())) {
                                            downvote++;
                                        }
                                    }

                                    ur.setTotalReactions(upvote - downvote);

                                    // Count each reaction type
                                    Map<String, Long> reactionsCount = reactionsForReview.stream()
                                            .collect(Collectors.groupingBy(
                                                    ReviewReaction::getType,
                                                    Collectors.counting()
                                            ));
                                    ur.setReactions(reactionsCount);

                                    // Count user reactions by reactionType (assuming it's per-user per-reaction type)
                                    Map<Long, String> userReactionCount = reactionsForReview.stream()
                                            .collect(Collectors.toMap(
                                                    rr -> rr.getId().getUserId(),
                                                    ReviewReaction::getType
                                            ));
                                    ur.setUserReactions(userReactionCount);

                                    return ur;
                                })
                ));
    }

    public ResponseEntity<ResponseMetaData> editReview(Long shopId,
                                                       Long reviewId,
                                                       EditReviewRequest editReviewRequest) {
        Review review = reviewRepository.findByIdAndCoffeeShopIdAndStatus(reviewId,
                shopId,
                Status.ACTIVE.getValue());
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

    public ResponseEntity<ResponseMetaData> react(Long reviewId,
                                                  String reaction) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        ReviewReaction.ReviewReactionId reviewReactionId = new ReviewReaction.ReviewReactionId(reviewId,
                SecurityUtil.getUserId());
        Optional<ReviewReaction> optionalReviewReaction = reviewReactionRepository.findById(reviewReactionId);
        if (optionalReviewReaction.isEmpty()) {
            ReviewReaction reviewReaction = new ReviewReaction(reviewReactionId,
                    reaction);
            reviewReactionRepository.save(reviewReaction);
        } else {
            ReviewReaction reviewReaction = optionalReviewReaction.get();
            if (reviewReaction.getType().equalsIgnoreCase(reaction)) {
                reviewReactionRepository.delete(reviewReaction);
                return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
            }
            reviewReaction.setType(reaction);
            reviewReactionRepository.save(reviewReaction);
        }
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> getReviews(PageDtoIn pageDtoIn,
                                                       String sortBy) {
        PageRequest pageRequest = PageRequest.of(pageDtoIn.getPage(),
                pageDtoIn.getSize());

        final Page<Review> reviews = getSortedReviews(sortBy, pageRequest);
        List<Long> reviewIds = reviews.getContent().stream().map(Review::getId).toList();
        final Map<Long, UserReaction> userReactionMap = groupedReactions(reviewIds);

        List<ReviewVM> reviewVMS = reviews.getContent()
                .stream()
                .map(r -> new ReviewVM(SecurityUtil.getUserId(),
                        r,
                        userReactionMap.getOrDefault(r.getId(),
                                null)))
                .toList();
        PageDtoOut<ReviewVM> pageDtoOut = PageDtoOut.from(pageDtoIn.getPage(),
                pageDtoIn.getSize(),
                reviews.getTotalElements(),
                reviewVMS);
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    public Page<Review> getSortedReviews(String reviewOrder, Pageable pageable) {
        return switch (reviewOrder) {
            case "trending" -> reviewRepository.findAllOrderByTrending(pageable);
            case "date_modified" -> reviewRepository.findAllByOrderByUpdatedAtDesc(pageable);
            case "date_created" -> reviewRepository.findAllByOrderByCreatedAtAsc(pageable);
            default -> reviewRepository.findAllOrderByScoreDesc(pageable);
        };
    }

    public ResponseEntity<ResponseMetaData> getReviewByUserId(User user,
                                                              String displayName,
                                                              PageDtoIn pageDtoIn) {
        Pageable pageable = PageRequest.of(pageDtoIn.getPage() - 1,
                pageDtoIn.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        User viewingUser = userRepository.findByDisplayNameAndStatus(displayName, Status.ACTIVE.getValue());
        if (Objects.isNull(viewingUser)) {
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.NOT_FOUND)));
        }
        Page<Review> reviews = reviewRepository.findAllByUserAndStatus(viewingUser,
                Status.ACTIVE.getValue(),
                pageable);
        List<Long> reviewIds = reviews.getContent().stream().map(Review::getId).toList();
        final Map<Long, UserReaction> userReactionMap = groupedReactions(reviewIds);

        List<ReviewVM> reviewVMS = reviews.getContent()
                .stream()
                .map(r -> new ReviewVM(user.getId(),
                        r,
                        userReactionMap.getOrDefault(r.getId(),
                                null)))
                .toList();
        final long totalElements = reviews.getTotalElements();
        PageDtoOut<ReviewVM> pageDtoOut = PageDtoOut.from(pageable.getPageNumber(),
                pageable.getPageSize(),
                totalElements,
                reviewVMS);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

//    public ResponseEntity<ResponseMetaData> getReviewReaction(final Long userId,
//                                                              String type,
//                                                              Long reviewId) {
//        final List<User> users = reviewReactionRepository.findByReviewIdAndType(reviewId,
//                type);
//        final Set<Long> relation = userFollowRepository.findRelationByIdIn(
//                users
//                        .stream()
//                        .map(u -> new UserFollow.UserFollowerId(u.getId(),
//                                userId))
//                        .toList());
//        final List<UserDTO> userDTOS = users.stream().map(u -> new UserDTO(u,
//                relation.contains(u.getId()))).toList();
//        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
//                userDTOS));
//    }
}
