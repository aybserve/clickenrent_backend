package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.ServiceProviderDTO;
import org.clickenrent.paymentservice.service.ServiceProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-providers")
@RequiredArgsConstructor
@Tag(name = "Service Provider", description = "Service provider management API")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    @GetMapping
    @Operation(summary = "Get all service providers")
    public ResponseEntity<List<ServiceProviderDTO>> getAll() {
        return ResponseEntity.ok(serviceProviderService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get service provider by ID")
    public ResponseEntity<ServiceProviderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceProviderService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get service provider by external ID")
    public ResponseEntity<ServiceProviderDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(serviceProviderService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new service provider")
    public ResponseEntity<ServiceProviderDTO> create(@Valid @RequestBody ServiceProviderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceProviderService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service provider")
    public ResponseEntity<ServiceProviderDTO> update(@PathVariable Long id, @Valid @RequestBody ServiceProviderDTO dto) {
        return ResponseEntity.ok(serviceProviderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete service provider")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceProviderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


