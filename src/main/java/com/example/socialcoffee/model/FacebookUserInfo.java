package com.example.socialcoffee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
public class FacebookUserInfo {

    @JsonProperty("id")
    private String id = StringUtils.EMPTY; //Default is empty

    @JsonProperty("name")
    private String name = StringUtils.EMPTY; //Default is empty

    @JsonProperty("email")
    private String email = StringUtils.EMPTY; //Default is empty

    @JsonProperty("birthday")
    private String birthday = StringUtils.EMPTY; //Default is empty

    @JsonProperty("gender")
    private String gender = StringUtils.EMPTY; //Default is empty

    @JsonProperty("picture")
    private Picture picture = new Picture();

    public String getPictureUrl() {
        return this.picture.pictureData.url;
    }

    @Data
    @NoArgsConstructor
    public static class Picture {
        @JsonProperty("data")
        private PictureData pictureData = new PictureData();
    }

    @Data
    @NoArgsConstructor
    public static class PictureData {

        @JsonProperty("height")
        private Long height = 0L; //Default is 0

        @JsonProperty("width")
        private Long width = 0L; //Default is O

        @JsonProperty("is_silhouette")
        private Boolean isSilhouette = false; //Default is false

        @JsonProperty("url")
        private String url = StringUtils.EMPTY; //Default is empty
    }
}
