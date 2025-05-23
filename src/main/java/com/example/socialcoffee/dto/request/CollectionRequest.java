package com.example.socialcoffee.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CollectionRequest {
    private String name;

    private String description;

    private MultipartFile file;
}
