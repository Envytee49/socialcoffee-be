package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.domain.Review;
import com.example.socialcoffee.model.UserReaction;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewVM {
    private Long id;
    private Integer rating;
    private String privacy;
    private String content;
    private List<Image> images;
    private Map<String, Long> reactions;
    private String createdDate;
    private UserDTO user;
    private CoffeeShopDTO coffeeShop;
    private String timeAgo;
    private Long totalReactions = 0L;
    private String reaction;
//    private Map<Long, String> userReactions;

    public ReviewVM(Long userId,
                    Review review,
                    UserReaction userReaction) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.content = review.getComment();
        this.images = review.getImages();
        this.createdDate = DateTimeUtil.covertLocalDateToString(review.getCreatedAt().toLocalDate());
        this.timeAgo = DateTimeUtil.getTimeAgo(review.getCreatedAt());
        if (Objects.nonNull(userReaction)) {
            this.totalReactions = userReaction.getTotalReactions();
//            this.userReactions = userReaction.getUserReactions();
            this.reactions = userReaction.getReactions();
            this.reaction = userReaction.getUserReactions().get(userId);
        }
        this.user = new UserDTO(review.getUser());
        this.coffeeShop = new CoffeeShopDTO(review.getCoffeeShop());

    }

    public ReviewVM(Long userId,
                    Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.content = review.getComment();
        this.images = review.getImages();
        this.createdDate = DateTimeUtil.covertLocalDateToString(review.getCreatedAt().toLocalDate());
        this.timeAgo = DateTimeUtil.getTimeAgo(review.getCreatedAt());
        this.user = new UserDTO(review.getUser());
        this.coffeeShop = new CoffeeShopDTO(review.getCoffeeShop());

    }
}
