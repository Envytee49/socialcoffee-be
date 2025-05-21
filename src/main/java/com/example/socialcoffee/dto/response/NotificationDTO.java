package com.example.socialcoffee.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Object meta;
    private String status;
    private String createdAt;
}
