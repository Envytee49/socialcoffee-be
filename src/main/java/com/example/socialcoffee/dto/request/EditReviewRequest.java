package com.example.socialcoffee.dto.request;

import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Setter
public class EditReviewRequest {

    private String content;
    private String sub_comment_id;
    private Boolean is_delete_resource = false;
    private MultipartFile resource;
    private String resource_sizes;

    public String getContent() {
        return StringUtils.trimToEmpty(content);
    }

    public String getSubCommentID() {
        return sub_comment_id;
    }

    public Boolean getIsDeleteResource() {
        return Objects.nonNull(is_delete_resource) ? is_delete_resource : false;
    }

    public MultipartFile getResource() {
        return resource;
    }

    public String getResourceSizes() {
        return StringUtils.trimToEmpty(resource_sizes);
    }

    public String getSecondSubCommentId() {
        return this.second_sub_comment_id;
    }
}