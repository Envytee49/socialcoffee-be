package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.Address;
import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.enums.RoleEnum;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.model.CrawlResponse;
import com.example.socialcoffee.repository.postgres.AddressRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CrawlDataService {
    private final CoffeeShopRepository coffeeShopRepository;

    private final AddressRepository addressRepository;

    private final ImageRepository imageRepository;

    @Transactional
    @GetMapping("/save")
    public void saveData() {
        RestClient restClient = RestClient.create();

        String requestBody = """
                {
                    "page": 1,
                    "limit": 1000,
                    "q": "Hà Nội"
                }
                """;

        // Sending a POST request
        CrawlResponse res = restClient
                .post()
                .uri(URI.create("https://api.dicaphekhong.com/api/search/brands"))
                .header("Content-Type",
                        "application/json") // Optional: Add headers if needed
                .body(requestBody) // Set the body for the POST request
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        for (var cf : res.getResult().getRows()) {
            log.info("Crawling {} {}",
                    cf.getId(),
                    cf.getName());
            Address address = Address.builder()
                    .addressDetail(cf.getAddress())
                    .ward(cf.getWard())
                    .district(cf.getDistrict())
                    .province(cf.getCity())
                    .longitude(cf.getLongitude())
                    .latitude(cf.getLatitude())
                    .build();

            address = addressRepository.save(address);
            List<Image> images = new ArrayList<>();
            for (var image : cf.getPreviewMedias()) {
                images.add(Image.builder().url(image).thumbnailUrl(image).build());
                images = imageRepository.saveAll(images);
            }
            CoffeeShop coffeeShop = CoffeeShop.builder()
                    .name(cf.getName())
                    .description(cf.getDescriptions())
                    .coverPhoto(cf.getCoverImage())
                    .phoneNumber(cf.getHotline())
                    .openHour(cf.getOpenTimeBySeconds() / 60)
                    .closeHour(cf.getCloseTimeBySeconds() / 60)
                    .status(Status.ACTIVE.getValue())
                    .createdBy((long) RoleEnum.ADMIN.ordinal())
                    .address(address)
                    .galleryPhotos(images)
                    .build();
            log.info("CoffeeShop: {}",
                    coffeeShop);
            coffeeShopRepository.save(coffeeShop);
        }
        log.info("Crawl done");
    }
}
