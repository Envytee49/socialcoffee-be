package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.domain.Review;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewVM {
    private Long id;
    private String title;
    private Integer rating;
    private String content;
    private Boolean isAnonymous;
    private List<Image> images;
    private LocalDateTime createdDate;
    private UserDTO user;
    private CoffeeShopDTO coffeeShop;
    public ReviewVM(Review review) {
        this.id = review.getId();
        this.isAnonymous = review.getIsAnonymous();
        this.rating = review.getRating();
        this.content = review.getComment();
        this.images = review.getImages();
        this.createdDate = review.getCreatedAt();
        this.user = new UserDTO(review.getUser());
        this.coffeeShop = new CoffeeShopDTO(review.getCoffeeShop());
    }
}
