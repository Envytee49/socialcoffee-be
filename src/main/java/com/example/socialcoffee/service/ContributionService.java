package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.postgres.CoffeeShop;
import com.example.socialcoffee.domain.postgres.CoffeeShopContribution;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.ContributionRequest;
import com.example.socialcoffee.dto.response.CoffeeShopDetailVM;
import com.example.socialcoffee.dto.response.ContributionVM;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.ContributionType;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.repository.postgres.CoffeeShopContributionRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.example.socialcoffee.utils.ObjectUtil.objectToString;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {
    private final CoffeeShopContributionRepository coffeeShopContributionRepository;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final CoffeeShopService coffeeShopService;

    private final NotificationService notificationService;

    private final CoffeeShopRepository coffeeShopRepository;

    private final ImageService imageService;

    private final CloudinaryService cloudinaryService;

    public ResponseEntity<ResponseMetaData> contributeCoffeeShop(User user,
                                                                 MultipartFile coverPhoto,
                                                                 MultipartFile[] galleryPhotos,
                                                                 ContributionRequest req) {
        log.info("Start create coffee shop with name = {}",
                req.getName());
        CoffeeShopContribution contribution = new CoffeeShopContribution();
        req.setGalleryPhotoPaths(imageService.getImagePath(galleryPhotos));
        req.setCoverPhotoPath(cloudinaryService.upload(coverPhoto));
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(objectMapper, req));
        contribution.setSubmittedBy(user);
        contribution.setStatus(Status.PENDING.getValue());
        contribution.setType(ContributionType.CONTRIBUTED.getValue());
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);
        CompletableFuture.runAsync(() -> notificationService.pushNotiToAdminWhenContribute(user.getDisplayName(),
                saved.getName()));
        log.info("Finish contribute coffee shop with name = {}, id = {}",
                req.getName(),
                saved.getId());
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> suggestAnEdit(User user,
                                                          MultipartFile coverPhoto,
                                                          MultipartFile[] galleryPhotos,
                                                          ContributionRequest req,
                                                          Long shopId) {
        CoffeeShop coffeeShop = coffeeShopRepository.findByShopId(shopId);
        if (Objects.isNull(coffeeShop)) throw new NotFoundException();
        // Retrieve existing contribution
        CoffeeShopContribution contribution = new CoffeeShopContribution();
        req.addNewGalleryPhotos(imageService.getImagePath(galleryPhotos));
        req.setCoverPhotoPath(coffeeShop.getCoverPhoto());
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(objectMapper, req));
        contribution.setSubmittedBy(user);
        contribution.setStatus(Status.PENDING.getValue());
        contribution.setType(ContributionType.SUGGESTED.getValue());
        contribution.setCoffeeShop(coffeeShop);
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);
        CompletableFuture.runAsync(() -> notificationService.pushNotiToAdminWhenSuggestAnEdit(user.getDisplayName(),
                saved.getName()));
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> editContribution(MultipartFile[] galleryPhotos,
                                                             ContributionRequest req,
                                                             Long contributionId) {
        final CoffeeShopContribution contribution = coffeeShopContributionRepository.findByCId(contributionId);
        if (Objects.isNull(contribution)) throw new NotFoundException();
        // Retrieve existing contribution
        req.addNewGalleryPhotos(imageService.getImagePath(galleryPhotos));
        contribution.setName(req.getName());
        contribution.setContribution(objectToString(objectMapper, req));
        if (contribution.getStatus().equalsIgnoreCase(Status.REJECTED.getValue())) {
            contribution.setStatus(Status.PENDING.getValue());
        }
        CoffeeShopContribution saved = coffeeShopContributionRepository.save(contribution);

        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    public ResponseEntity<ResponseMetaData> getContributions(Long userId,
                                                             String name,
                                                             String status,
                                                             String type,
                                                             PageRequest pageRequest) {
        User user = null;
        Page<CoffeeShopContribution> contributions;
        if (Objects.nonNull(userId)) {
            user = userRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
        }
        if (Objects.nonNull(user) && StringUtils.isNotBlank(name)) {
            contributions = coffeeShopContributionRepository.findByStatusAndTypeAndSubmittedByAndName(status,
                    type,
                    user,
                    name,
                    pageRequest);
        } else if (Objects.nonNull(user)) {
            contributions = coffeeShopContributionRepository.findByStatusAndTypeAndSubmittedBy(status,
                    type,
                    user,
                    pageRequest);
        } else if (StringUtils.isNotBlank(name)) {
            contributions = coffeeShopContributionRepository.findByStatusAndTypeAndName(status,
                    type,
                    name,
                    pageRequest);
        } else {
            contributions = coffeeShopContributionRepository.findByStatusAndType(status,
                    type,
                    pageRequest);
        }
        List<ContributionVM> contributionVMS = contributions.getContent().stream().map(c -> {
            ContributionVM contributionVM = new ContributionVM();
            contributionVM.setId(c.getId());
            contributionVM.setCreatedAt(DateTimeUtil.convertLocalDateToString(c.getCreatedAt()));
            contributionVM.setUpdatedAt(DateTimeUtil.convertLocalDateToString(c.getUpdatedAt()));
            contributionVM.setSubmittedBy(c.getSubmittedBy().getDisplayName());
            contributionVM.setStatus(c.getStatus());
            contributionVM.setComment(c.getReviewComments());
            contributionVM.setCoffeeShop(new CoffeeShopDetailVM(c.getCoffeeShop()));
            try {
                final ContributionRequest contributionRequest = objectMapper.readValue(c.getContribution(),
                        ContributionRequest.class);
                contributionVM.setData(contributionRequest);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return contributionVM;
        }).toList();
        PageDtoOut<ContributionVM> pageDtoOut = PageDtoOut.from(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                contributions.getTotalElements(),
                contributionVMS
        );
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                pageDtoOut));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> approveContribution(Long id,
                                                                User currentUser,
                                                                String comment) {
        CoffeeShopContribution contribution = coffeeShopContributionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contribution not found"));
        contribution.setStatus(Status.APPROVED.getValue());
        contribution.setReviewComments(comment);
        contribution.setReviewedBy(currentUser);
        coffeeShopContributionRepository.save(contribution);
        ContributionRequest contributionRequest = null;
        try {
            contributionRequest = objectMapper.readValue(contribution.getContribution(),
                    ContributionRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        coffeeShopService.createCoffeeShop(contributionRequest);
        CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenApproveContribution(contribution.getSubmittedBy(), contribution.getName()));

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> rejectContribution(Long id,
                                                               User currentUser,
                                                               String comment) {
        CoffeeShopContribution contribution = coffeeShopContributionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contribution not found"));
        contribution.setStatus(Status.REJECTED.getValue());
        contribution.setReviewComments(comment);
        contribution.setReviewedBy(currentUser);
        coffeeShopContributionRepository.save(contribution);
        CompletableFuture.runAsync(() -> notificationService.pushNotiToUsersWhenRejectContribution(contribution.getSubmittedBy(), contribution.getName()));

        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS)));
    }
}
