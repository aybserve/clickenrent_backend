package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ServiceProductDTO;
import org.clickenrent.rentalservice.service.ServiceProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-products")
@RequiredArgsConstructor
@Tag(name = "ServiceProduct", description = "Service product management")
@SecurityRequirement(name = "bearerAuth")
public class ServiceProductController {

    private final ServiceProductService serviceProductService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all service products")
    public ResponseEntity<Page<ServiceProductDTO>> getAllServiceProducts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(serviceProductService.getAllServiceProducts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get service product by ID")
    public ResponseEntity<ServiceProductDTO> getServiceProductById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceProductService.getServiceProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create service product")
    public ResponseEntity<ServiceProductDTO> createServiceProduct(@Valid @RequestBody ServiceProductDTO dto) {
        return new ResponseEntity<>(serviceProductService.createServiceProduct(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete service product")
    public ResponseEntity<Void> deleteServiceProduct(@PathVariable Long id) {
        serviceProductService.deleteServiceProduct(id);
        return ResponseEntity.noContent().build();
    }
}

