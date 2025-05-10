package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.Address;
import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.Image;
import com.example.socialcoffee.domain.feature.*;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CoffeeShopDetailVM {
    private Long id;
    private String name;
    private String coverPhoto;
    private String phoneNumber;
    private String webAddress;
    private String menuWebAddress;
    private String additionInfo;
    private String openHour;
    private String closeHour;
    private String isOpen;
    private String status;
    private List<Image> galleryPhotos;
    private Address address;
    private String description;
    private Double averageRating;
    private Long reviewCounts;
    private List<FeatureDTO.AmbianceDto> ambiances;
    private List<FeatureDTO.AmenityDto> amenities;
    private List<FeatureDTO.CapacityDto> capacities;
    private List<FeatureDTO.CategoryDto> categories;
    private List<FeatureDTO.DressCodeDto> dressCodes;
    private List<FeatureDTO.EntertainmentDto> entertainments;
    private List<FeatureDTO.ParkingDto> parkings;
    private List<FeatureDTO.PriceDto> prices;
    private List<FeatureDTO.PurposeDto> purposes;
    private List<FeatureDTO.ServiceTypeDto> serviceTypes;
    private List<FeatureDTO.SpaceDto> spaces;
    private List<FeatureDTO.SpecialtyDto> specialties;
    private List<FeatureDTO.VisitTimeDto> visitTimes;

    public CoffeeShopDetailVM(CoffeeShop coffeeShop) {
        this.id = coffeeShop.getId();
        this.name = coffeeShop.getName();
        this.coverPhoto = coffeeShop.getCoverPhoto();
        this.phoneNumber = coffeeShop.getPhoneNumber();
        this.webAddress = coffeeShop.getWebAddress();
        this.menuWebAddress = coffeeShop.getMenuWebAddress();
        this.additionInfo = coffeeShop.getAdditionInfo();
        this.openHour = DateTimeUtil.convertMinuteToHour(coffeeShop.getOpenHour());
        this.closeHour = DateTimeUtil.convertMinuteToHour(coffeeShop.getCloseHour());
        this.galleryPhotos = coffeeShop.getGalleryPhotos();
        this.address = coffeeShop.getAddress();
        this.status = DateTimeUtil.checkCurrentOpenStatus(coffeeShop.getOpenHour(),
                                                          coffeeShop.getCloseHour());
        this.averageRating = NumberUtil.roundToTwoDecimals(coffeeShop.getAverageRating());
        this.reviewCounts = coffeeShop.getReviewCount();
        this.description = coffeeShop.getDescription();
        mapFeaturesFromEntity(coffeeShop);
    }
    public void mapFeaturesFromEntity(CoffeeShop coffeeShop) {
        this.ambiances = coffeeShop.getAmbiances().stream()
                .map(amb -> {
                    FeatureDTO.AmbianceDto dto = new FeatureDTO.AmbianceDto();
                    BeanUtils.copyProperties(amb, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.amenities = coffeeShop.getAmenities().stream()
                .map(a -> {
                    FeatureDTO.AmenityDto dto = new FeatureDTO.AmenityDto();
                    BeanUtils.copyProperties(a, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.capacities = coffeeShop.getCapacities().stream()
                .map(c -> {
                    FeatureDTO.CapacityDto dto = new FeatureDTO.CapacityDto();
                    BeanUtils.copyProperties(c, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.categories = coffeeShop.getCategories().stream()
                .map(c -> {
                    FeatureDTO.CategoryDto dto = new FeatureDTO.CategoryDto();
                    BeanUtils.copyProperties(c, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.dressCodes = coffeeShop.getDressCodes().stream()
                .map(dc -> {
                    FeatureDTO.DressCodeDto dto = new FeatureDTO.DressCodeDto();
                    BeanUtils.copyProperties(dc, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.entertainments = coffeeShop.getEntertainments().stream()
                .map(e -> {
                    FeatureDTO.EntertainmentDto dto = new FeatureDTO.EntertainmentDto();
                    BeanUtils.copyProperties(e, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.parkings = coffeeShop.getParkings().stream()
                .map(p -> {
                    FeatureDTO.ParkingDto dto = new FeatureDTO.ParkingDto();
                    BeanUtils.copyProperties(p, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.prices = coffeeShop.getPrices().stream()
                .map(p -> {
                    FeatureDTO.PriceDto dto = new FeatureDTO.PriceDto();
                    BeanUtils.copyProperties(p, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.purposes = coffeeShop.getPurposes().stream()
                .map(p -> {
                    FeatureDTO.PurposeDto dto = new FeatureDTO.PurposeDto();
                    BeanUtils.copyProperties(p, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.serviceTypes = coffeeShop.getServiceTypes().stream()
                .map(s -> {
                    FeatureDTO.ServiceTypeDto dto = new FeatureDTO.ServiceTypeDto();
                    BeanUtils.copyProperties(s, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.spaces = coffeeShop.getSpaces().stream()
                .map(s -> {
                    FeatureDTO.SpaceDto dto = new FeatureDTO.SpaceDto();
                    BeanUtils.copyProperties(s, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.specialties = coffeeShop.getSpecialties().stream()
                .map(s -> {
                    FeatureDTO.SpecialtyDto dto = new FeatureDTO.SpecialtyDto();
                    BeanUtils.copyProperties(s, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());

        this.visitTimes = coffeeShop.getVisitTimes().stream()
                .map(v -> {
                    FeatureDTO.VisitTimeDto dto = new FeatureDTO.VisitTimeDto();
                    BeanUtils.copyProperties(v, dto);
                    dto.setSet(false);
                    return dto;
                }).collect(Collectors.toList());
    }
    public void setFeatureDto(SearchFilter preference) {
        if (preference.getAmbiances() != null
                && this.ambiances != null) {
            var ids = preference.getAmbiances().stream().map(Ambiance::getId).toList();
            this.ambiances.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        } else

        if (preference.getAmenities() != null && this.amenities != null) {
            var ids = preference.getAmenities().stream().map(Amenity::getId).toList();
            this.amenities.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getCapacities() != null && this.capacities != null) {
            var ids = preference.getCapacities().stream().map(Capacity::getId).toList();
            this.capacities.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getCategories() != null && this.categories != null) {
            var ids = preference.getCategories().stream().map(Category::getId).toList();
            this.categories.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getEntertainments() != null && this.entertainments != null) {
            var ids = preference.getEntertainments().stream().map(Entertainment::getId).toList();
            this.entertainments.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getParkings() != null && this.parkings != null) {
            var ids = preference.getParkings().stream().map(Parking::getId).toList();
            this.parkings.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getPrices() != null && this.prices != null) {
            var ids = preference.getPrices().stream().map(Price::getId).toList();
            this.prices.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getPurposes() != null && this.purposes != null) {
            var ids = preference.getPurposes().stream().map(Purpose::getId).toList();
            this.purposes.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getServiceTypes() != null && this.serviceTypes != null) {
            var ids = preference.getServiceTypes().stream().map(ServiceType::getId).toList();
            this.serviceTypes.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getSpaces() != null && this.spaces != null) {
            var ids = preference.getSpaces().stream().map(Space::getId).toList();
            this.spaces.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getSpecialties() != null && this.specialties != null) {
            var ids = preference.getSpecialties().stream().map(Specialty::getId).toList();
            this.specialties.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }

        if (preference.getVisitTimes() != null && this.visitTimes != null) {
            var ids = preference.getVisitTimes().stream().map(VisitTime::getId).toList();
            this.visitTimes.forEach(dto -> dto.setSet(ids.contains(dto.getId())));
        }
    }

    public void setFeatureDto(CoffeeShopSearchRequest filter) {
        if (filter.getAmbiances() != null
                && this.ambiances != null) {
            var ids = filter.getAmbiances();
            this.ambiances.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        } else

        if (filter.getAmenities() != null && this.amenities != null) {
            var ids = filter.getAmenities();
            this.amenities.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getCapacities() != null && this.capacities != null) {
            var ids = filter.getCapacities();
            this.capacities.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getCategories() != null && this.categories != null) {
            var ids = filter.getCategories();
            this.categories.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getEntertainments() != null && this.entertainments != null) {
            var ids = filter.getEntertainments();
            this.entertainments.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getParkings() != null && this.parkings != null) {
            var ids = filter.getParkings();
            this.parkings.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getPrices() != null && this.prices != null) {
            var ids = filter.getPrices();
            this.prices.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getPurposes() != null && this.purposes != null) {
            var ids = filter.getPurposes();
            this.purposes.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getServiceTypes() != null && this.serviceTypes != null) {
            var ids = filter.getServiceTypes();
            this.serviceTypes.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getSpaces() != null && this.spaces != null) {
            var ids = filter.getSpaces();
            this.spaces.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getSpecialties() != null && this.specialties != null) {
            var ids = filter.getSpecialties();
            this.specialties.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }

        if (filter.getVisitTimes() != null && this.visitTimes != null) {
            var ids = filter.getVisitTimes();
            this.visitTimes.forEach(dto -> dto.setInSearchPrompt(ids.contains(dto.getId())));
        }
    }
}
