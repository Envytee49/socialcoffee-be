package com.example.socialcoffee.controller;

import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.ContributionType;
import com.example.socialcoffee.service.CoffeeShopContributionService;
import com.example.socialcoffee.service.CoffeeShopService;
import com.example.socialcoffee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController extends BaseController {
    private final CoffeeShopService coffeeShopService;
    private final UserService userService;

    private final CoffeeShopContributionService contributionService;

    // Get filtered contributions or edit requests
    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> getRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) ContributionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return contributionService.getFilteredContributions(status, type.getValue(), pageRequest);
    }

    // Get contribution details
    @GetMapping("/contributions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMetaData> getContributionDetails(@PathVariable Long id) {
        return contributionService.getContributionById(id);
    }

    // Approve contribution
    @PostMapping("/contributions/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveContribution(
            @PathVariable Long id,
            @RequestParam String comment) {
        contributionService.approveContribution(id, getCurrentUser(), comment);
        return ResponseEntity.ok("Contribution approved successfully");
    }

    // Reject contribution
    @PostMapping("/contributions/{id}/decline")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> declineContribution(
            @PathVariable Long id,
            @RequestParam String comment) {
        contributionService.rejectContribution(id, getCurrentUser(), comment);
        return ResponseEntity.ok("Contribution declined successfully");
    }
}
