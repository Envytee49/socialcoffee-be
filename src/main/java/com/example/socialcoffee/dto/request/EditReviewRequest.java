package com.example.socialcoffee.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class EditReviewRequest {

    private Integer rating;

    private String title;

    private String content;

    private String subCommentId;

    private List<EditReviewImage> editReviewImages;

    private MultipartFile[] resources;

    private String resource_sizes;

    @Getter
    public static class EditReviewImage {
        private Long id;

        private Boolean isDeleteResource = false;
    }
}