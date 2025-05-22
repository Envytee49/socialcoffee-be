package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.CoffeeShopContribution;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.common.PageDtoOut;
import com.example.socialcoffee.dto.request.ContributionRequest;
import com.example.socialcoffee.dto.response.ContributionVM;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.repository.postgres.CoffeeShopContributionRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {
    private final CoffeeShopContributionRepository coffeeShopContributionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CoffeeShopService coffeeShopService;
    private final NotificationService notificationService;

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
            contributionVM.setCreatedAt(DateTimeUtil.covertLocalDateToString(c.getCreatedAt()));
            contributionVM.setUpdatedAt(DateTimeUtil.covertLocalDateToString(c.getUpdatedAt()));
            contributionVM.setSubmittedBy(c.getSubmittedBy().getDisplayName());
            contributionVM.setStatus(c.getStatus());
            contributionVM.setComment(c.getReviewComments());
            try {
                contributionVM.setData(objectMapper.readValue(c.getContribution(),
                                                              ContributionRequest.class));
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

        coffeeShopService.createCoffeeShop(contribution.getSubmittedBy(),
                                           contributionRequest);
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
