package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.service.UserPaymentProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-payment-profiles")
@RequiredArgsConstructor
@Tag(name = "User Payment Profile", description = "User payment profile management API")
public class UserPaymentProfileController {

    private final UserPaymentProfileService userPaymentProfileService;

    @GetMapping
    @Operation(summary = "Get all user payment profiles")
    public ResponseEntity<List<UserPaymentProfileDTO>> getAll() {
        return ResponseEntity.ok(userPaymentProfileService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment profile by ID")
    public ResponseEntity<UserPaymentProfileDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userPaymentProfileService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get payment profile by external ID")
    public ResponseEntity<UserPaymentProfileDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(userPaymentProfileService.findByExternalId(externalId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment profile by user ID")
    public ResponseEntity<UserPaymentProfileDTO> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userPaymentProfileService.findByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "Create new payment profile")
    public ResponseEntity<UserPaymentProfileDTO> create(@Valid @RequestBody UserPaymentProfileDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPaymentProfileService.create(dto));
    }

    @PostMapping("/create-or-get/{userId}")
    @Operation(summary = "Create or get payment profile for user")
    public ResponseEntity<UserPaymentProfileDTO> createOrGetProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userPaymentProfileService.createOrGetProfile(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment profile")
    public ResponseEntity<UserPaymentProfileDTO> update(@PathVariable Long id, @Valid @RequestBody UserPaymentProfileDTO dto) {
        return ResponseEntity.ok(userPaymentProfileService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment profile")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userPaymentProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
