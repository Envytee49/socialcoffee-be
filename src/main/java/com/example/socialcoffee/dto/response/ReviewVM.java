package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.domain.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewVM {
    private Long id;
    private String title;
    private Integer rating;
    private String content;
    private Boolean isAnonymous;
    private List<Image> images;
    private Map<String, Long> reactions;

    public ReviewVM(Review review, Map<String, Long> reactions) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.isAnonymous = review.getIsAnonymous();
        this.rating = review.getRating();
        this.content = review.getComment();
        this.images = review.getImages();
        this.reactions = reactions;
    }
}
