package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CityDTO;
import org.clickenrent.authservice.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for City management operations.
 */
@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Tag(name = "City", description = "City management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CityController {
    
    private final CityService cityService;
    
    /**
     * Get all cities.
     * GET /api/cities
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get all cities", description = "Retrieve a list of all cities")
    public ResponseEntity<List<CityDTO>> getAllCities() {
        List<CityDTO> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }
    
    /**
     * Get city by ID.
     * GET /api/cities/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get city by ID", description = "Retrieve a specific city by its ID")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long id) {
        CityDTO city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }
    
    /**
     * Get cities by country ID.
     * GET /api/cities/by-country/{countryId}
     */
    @GetMapping("/by-country/{countryId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get cities by country", description = "Retrieve all cities in a specific country")
    public ResponseEntity<List<CityDTO>> getCitiesByCountryId(@PathVariable Long countryId) {
        List<CityDTO> cities = cityService.getCitiesByCountryId(countryId);
        return ResponseEntity.ok(cities);
    }
    
    /**
     * Create a new city.
     * POST /api/cities
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create city", description = "Create a new city")
    public ResponseEntity<CityDTO> createCity(@Valid @RequestBody CityDTO cityDTO) {
        CityDTO createdCity = cityService.createCity(cityDTO);
        return new ResponseEntity<>(createdCity, HttpStatus.CREATED);
    }
    
    /**
     * Update city by ID.
     * PUT /api/cities/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update city", description = "Update an existing city")
    public ResponseEntity<CityDTO> updateCity(
            @PathVariable Long id,
            @Valid @RequestBody CityDTO cityDTO) {
        CityDTO updatedCity = cityService.updateCity(id, cityDTO);
        return ResponseEntity.ok(updatedCity);
    }
    
    /**
     * Delete city by ID.
     * DELETE /api/cities/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete city", description = "Delete a city by its ID")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}

