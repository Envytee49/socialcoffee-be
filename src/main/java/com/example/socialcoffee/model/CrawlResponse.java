package com.example.socialcoffee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrawlResponse {

    private Result result;

    @Getter
    @Setter
    public static class Result {

        private Meta meta;

        private List<CoffeeMapper> rows;
    }

    @Getter
    @Setter
    public static class Meta {
        private int page;

        @JsonProperty("perPage")
        private int perPage;

        private int total;
    }

    @Getter
    @Setter
    public static class CoffeeMapper {
        // Add properties if rows will contain data in the future
        private String id;

        private String name;

        private boolean isPublic;

        private String descriptions;

        private String coverImage;

        private String parking;

        private int openTimeBySeconds;

        private int closeTimeBySeconds;

        private String hotline;

        private List<String> previewMedias;

        private String address;

        private String ward;

        private String district;

        private String city;

        private int priceAverage;

        private int priceMin;

        private int priceMax;

        private String slug;

        private double latitude;

        private double longitude;

        @JsonProperty("is24h")
        private boolean is24h;

        @JsonProperty("isSponsor")
        private boolean isSponsor;

        private String wifiPassword;

        private String googlePlaceId;

        @JsonProperty("isDraft")
        private boolean isDraft;

        private String contributorId;

        @Getter
        @Setter
        static class Media {
            private String id;

            private String url;

            private String type;

            private String createdAt;

            private String brandId;
        }
    }
}


