package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.dto.common.AddressDTO;
import com.example.socialcoffee.dto.common.ImageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoffeeShopDTO {
    private Long id;
    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String description;
    private Integer openHour;
    private Integer closeHour;
    private String status;
    private AddressDTO address;
    private Long createdBy;
    private List<ImageDTO> galleryPhotos;
    public CoffeeShopDTO(CoffeeShop coffeeShop) {
        this.id = coffeeShop.getId();
        this.name = coffeeShop.getName();
    }
}
