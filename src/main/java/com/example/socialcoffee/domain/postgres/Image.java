package com.example.socialcoffee.domain.postgres;

import com.example.socialcoffee.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "images")
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String thumbnailUrl;

    private double width;

    private double height;

    private double size;

    private String status = Status.ACTIVE.getValue();

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
