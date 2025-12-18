package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.FeedbackDTO;
import org.clickenrent.supportservice.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Feedback entities.
 */
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Feedback management")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all feedbacks (or user's own feedbacks)")
    public ResponseEntity<List<FeedbackDTO>> getAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get feedback by ID")
    public ResponseEntity<FeedbackDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get feedback by external ID")
    public ResponseEntity<FeedbackDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(feedbackService.getByExternalId(externalId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get feedbacks by user ID")
    public ResponseEntity<List<FeedbackDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.getByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create feedback")
    public ResponseEntity<FeedbackDTO> create(@Valid @RequestBody FeedbackDTO dto) {
        return new ResponseEntity<>(feedbackService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update feedback")
    public ResponseEntity<FeedbackDTO> update(@PathVariable Long id, @Valid @RequestBody FeedbackDTO dto) {
        return ResponseEntity.ok(feedbackService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete feedback")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


