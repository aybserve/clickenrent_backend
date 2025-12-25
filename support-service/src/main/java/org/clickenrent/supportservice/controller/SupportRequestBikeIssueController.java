package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestBikeIssueDTO;
import org.clickenrent.supportservice.service.SupportRequestBikeIssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing SupportRequestBikeIssue junction entities.
 */
@RestController
@RequestMapping("/api/support-request-bike-issues")
@RequiredArgsConstructor
@Tag(name = "Support Request Bike Issue", description = "Support request bike issue link management")
@SecurityRequirement(name = "bearerAuth")
public class SupportRequestBikeIssueController {

    private final SupportRequestBikeIssueService supportRequestBikeIssueService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all support request bike issue links (admin only)")
    public ResponseEntity<List<SupportRequestBikeIssueDTO>> getAll() {
        return ResponseEntity.ok(supportRequestBikeIssueService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support request bike issue link by ID")
    public ResponseEntity<SupportRequestBikeIssueDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supportRequestBikeIssueService.getById(id));
    }

    @GetMapping("/support-request/{supportRequestId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike issues by support request ID")
    public ResponseEntity<List<SupportRequestBikeIssueDTO>> getBySupportRequestId(@PathVariable Long supportRequestId) {
        return ResponseEntity.ok(supportRequestBikeIssueService.getBySupportRequestId(supportRequestId));
    }

    @GetMapping("/bike-issue/{bikeIssueId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get support requests by bike issue ID (admin only)")
    public ResponseEntity<List<SupportRequestBikeIssueDTO>> getByBikeIssueId(@PathVariable Long bikeIssueId) {
        return ResponseEntity.ok(supportRequestBikeIssueService.getByBikeIssueId(bikeIssueId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create support request bike issue link")
    public ResponseEntity<SupportRequestBikeIssueDTO> create(@Valid @RequestBody SupportRequestBikeIssueDTO dto) {
        return new ResponseEntity<>(supportRequestBikeIssueService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete support request bike issue link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportRequestBikeIssueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}






