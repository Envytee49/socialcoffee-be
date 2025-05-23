package com.example.socialcoffee.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "shop_users")
@Entity
public class ShopUser {
    @EmbeddedId
    private ShopUserId id;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    private String status;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopUserId implements Serializable {
        private Long userId;

        private Long shopId;
    }
}
