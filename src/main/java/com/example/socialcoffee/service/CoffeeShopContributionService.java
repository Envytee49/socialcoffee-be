package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.CoffeeShop;
import com.example.socialcoffee.domain.CoffeeShopContribution;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.postgres.CoffeeShopContributionRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeShopContributionService {
    private final CoffeeShopContributionRepository contributionRepository;

    private final CoffeeShopRepository coffeeShopRepository;

    public ResponseEntity<ResponseMetaData> getFilteredContributions(String status, String type, PageRequest pageRequest) {
        Page<CoffeeShopContribution> contributions;
        if (status != null && type != null) {
            contributions = contributionRepository.findByStatusAndType(status, type, pageRequest);
        } else if (status != null) {
            contributions = contributionRepository.findByStatus(status, pageRequest);
        } else if (type != null) {
            contributions = contributionRepository.findByType(type, pageRequest);
        } else {
            contributions = contributionRepository.findAll(pageRequest);
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), contributions));
    }

    public CoffeeShopContribution getContributionById(Long id) {
        return contributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contribution not found"));
    }

    @Transactional
    public void approveContribution(Long contributionId, User admin, String comments) {
        CoffeeShopContribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution not found"));

        // Create new CoffeeShop entity
        CoffeeShop coffeeShop = CoffeeShop.builder()
                .name(contribution.getName())
                .coverPhoto(contribution.getCoverPhoto())
                .phoneNumber(contribution.getPhoneNumber())
                .webAddress(contribution.getWebAddress())
                .menuWebAddress(contribution.getMenuWebAddress())
                .additionInfo(contribution.getAdditionalInfo())
                .openHour(contribution.getOpenHour())
                .closeHour(contribution.getCloseHour())
                .galleryPhotos(contribution.getGalleryPhotos())
                .address(contribution.getAddress())
                .description(contribution.getDescription())
                .status(Status.ACTIVE.getValue())
                .createdBy(contribution.getSubmittedBy().getId())
                .ambiances(contribution.getAmbiances())
                .amenities(contribution.getAmenities())
                .capacities(contribution.getCapacities())
                .categories(contribution.getCategories())
                .dressCodes(contribution.getDressCodes())
                .entertainments(contribution.getEntertainments())
                .parkings(contribution.getParkings())
                .prices(contribution.getPrices())
                .serviceTypes(contribution.getServiceTypes())
                .spaces(contribution.getSpaces())
                .specialties(contribution.getSpecialties())
                .visitTimes(contribution.getVisitTimes())
                .build();

        coffeeShopRepository.save(coffeeShop);

        // Update contribution status
        contribution.setStatus(Status.APPROVED.getValue());
        contribution.setReviewedBy(admin);
        contribution.setReviewComments(comments);
        contributionRepository.save(contribution);

        contributionRepository.delete(contribution);
    }

    @Transactional
    public void rejectContribution(Long contributionId, User admin, String comments) {
        CoffeeShopContribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution not found"));

        // Update contribution status
        contribution.setStatus(Status.REJECTED.getValue());
        contribution.setReviewedBy(admin);
        contribution.setReviewComments(comments);
        contributionRepository.save(contribution);
    }
}
