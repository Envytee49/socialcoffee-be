package com.example.socialcoffee.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        try{
            if(Objects.isNull(file)) return StringUtils.EMPTY;
            log.info("Start uploading {}", file.getOriginalFilename());
            final Map data = this.cloudinary.uploader().upload(file.getBytes(),
                                                                 Map.of());
            log.info("Finish uploading {}", file.getOriginalFilename());
            return (String) data.get("url");
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }

}

