package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;
    public List<Image> save(MultipartFile[] files) {
        if(Objects.isNull(files) || files.length == 0) return new ArrayList<>();
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = cloudinaryService.upload(file);
            Image image = Image.builder()
                    .url(url)
                    .thumbnailUrl(url)
                    .build();
            images.add(image);
        }
        return imageRepository.saveAll(images);
    }
}
