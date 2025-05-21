package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.dto.request.ContributionRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContributionVM {
    private Long id;
    private ContributionRequest data;
    private String status;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
