package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.UserPaymentMethodDTO;
import org.clickenrent.paymentservice.service.UserPaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-payment-methods")
@RequiredArgsConstructor
@Tag(name = "User Payment Method", description = "User payment method management API")
public class UserPaymentMethodController {

    private final UserPaymentMethodService userPaymentMethodService;

    @GetMapping
    @Operation(summary = "Get all user payment methods")
    public ResponseEntity<List<UserPaymentMethodDTO>> getAll() {
        return ResponseEntity.ok(userPaymentMethodService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment method by ID")
    public ResponseEntity<UserPaymentMethodDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userPaymentMethodService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get payment method by external ID")
    public ResponseEntity<UserPaymentMethodDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(userPaymentMethodService.findByExternalId(externalId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment methods by user ID")
    public ResponseEntity<List<UserPaymentMethodDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userPaymentMethodService.findByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "Create new payment method")
    public ResponseEntity<UserPaymentMethodDTO> create(@Valid @RequestBody UserPaymentMethodDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPaymentMethodService.create(dto));
    }

    @PostMapping("/attach")
    @Operation(summary = "Attach payment method to profile")
    public ResponseEntity<UserPaymentMethodDTO> attachPaymentMethod(
            @RequestParam Long profileId,
            @RequestParam String stripePaymentMethodId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userPaymentMethodService.attachPaymentMethod(profileId, stripePaymentMethodId));
    }

    @PostMapping("/{id}/set-default")
    @Operation(summary = "Set payment method as default")
    public ResponseEntity<UserPaymentMethodDTO> setDefaultPaymentMethod(@PathVariable Long id) {
        return ResponseEntity.ok(userPaymentMethodService.setDefaultPaymentMethod(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment method")
    public ResponseEntity<UserPaymentMethodDTO> update(@PathVariable Long id, @Valid @RequestBody UserPaymentMethodDTO dto) {
        return ResponseEntity.ok(userPaymentMethodService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment method")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userPaymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
