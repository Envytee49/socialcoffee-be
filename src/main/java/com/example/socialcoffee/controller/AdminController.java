package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.request.CoffeeShopSearchRequest;
import com.example.socialcoffee.dto.request.CreateCoffeeShopRequest;
import com.example.socialcoffee.dto.response.DashboardVM;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.ContributionType;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.repository.postgres.CoffeeShopContributionRepository;
import com.example.socialcoffee.repository.postgres.CoffeeShopRepository;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.ContributionService;
import com.example.socialcoffee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController extends BaseController {
    private final CoffeeShopService coffeeShopService;

    private final ContributionService contributionService;

    private final CoffeeShopRepository coffeeShopRepository;

    private final CoffeeShopContributionRepository coffeeShopContributionRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> getRequests() {
        DashboardVM dashboardVM = new DashboardVM();
        dashboardVM.setTotalCoffeeShops(coffeeShopRepository.countByStatus(Status.ACTIVE.getValue()));
        dashboardVM.setPendingApprovals(coffeeShopContributionRepository.countByStatus(Status.PENDING.getValue()));
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                dashboardVM));
    }

    @PostMapping(value = "/coffee-shops", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> createCoffeeShop(@ModelAttribute CreateCoffeeShopRequest request) {
        return coffeeShopService.createCoffeeShop(request);
    }

    @PutMapping(value = "/coffee-shops/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> updateCoffeeShop(@ModelAttribute CreateCoffeeShopRequest request,
                                                             @PathVariable Long id) {
        return coffeeShopService.updateCoffeeShop(request,
                id);
    }

    @DeleteMapping(value = "/coffee-shops/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> deleteCoffeeShop(@PathVariable Long id) {
        return coffeeShopService.deleteCoffeeShop(id);
    }

    @PutMapping(value = "/coffee-shops/{id}/sponsor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> sponsorCoffeeShop(@PathVariable Long id) {
        return coffeeShopService.sponsorCoffeeShop(id);
    }


    @GetMapping("/coffee-shops")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> getCoffeeShops(
            @RequestParam(required = false) String name,
            PageDtoIn pageDtoIn
    ) {
        CoffeeShopSearchRequest coffeeShopSearchRequest = CoffeeShopSearchRequest
                .builder()
                .name(name)
                .build();
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                coffeeShopService.search(coffeeShopSearchRequest,
                        pageDtoIn.getPage() - 1,
                        pageDtoIn.getSize(),
                        Sort.by(Sort.Direction.DESC, "isSponsored"), false)));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> getRequests(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String name,
            @RequestParam String status,
            @RequestParam ContributionType type,
            PageDtoIn pageDtoIn) {
        PageRequest pageRequest = PageRequest.of(pageDtoIn.getPage() - 1,
                pageDtoIn.getSize());
        return contributionService.getContributions(userId,
                name,
                status,
                type.getValue(),
                pageRequest);
    }

    @PutMapping("/contributions/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> approveContribution(
            @PathVariable Long id,
            @RequestParam String comment) {
        return contributionService.approveContribution(id,
                getCurrentUser(),
                comment);
    }

    @PutMapping("/contributions/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> rejectContribution(
            @PathVariable Long id,
            @RequestParam String comment) {
        return contributionService.rejectContribution(id,
                getCurrentUser(),
                comment);
    }
}
