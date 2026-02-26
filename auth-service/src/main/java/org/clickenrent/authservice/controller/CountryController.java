package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CountryDTO;
import org.clickenrent.authservice.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Country management operations.
 */
@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
@Tag(name = "Country", description = "Country management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CountryController {
    
    private final CountryService countryService;
    
    /**
     * Get all countries.
     * GET /api/countries
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get all countries", description = "Retrieve a list of all countries")
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        List<CountryDTO> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }
    
    /**
     * Get country by ID.
     * GET /api/countries/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get country by ID", description = "Retrieve a specific country by its ID")
    public ResponseEntity<CountryDTO> getCountryById(@PathVariable Long id) {
        CountryDTO country = countryService.getCountryById(id);
        return ResponseEntity.ok(country);
    }
    
    /**
     * Get country by name.
     * GET /api/countries/by-name?name={name}
     */
    @GetMapping("/by-name")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get country by name", description = "Retrieve a country by its name")
    public ResponseEntity<CountryDTO> getCountryByName(@RequestParam String name) {
        CountryDTO country = countryService.getCountryByName(name);
        return ResponseEntity.ok(country);
    }
    
    /**
     * Get country by external ID.
     * GET /api/countries/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get country by external ID", description = "Retrieve a country by its external ID")
    public ResponseEntity<CountryDTO> getCountryByExternalId(@PathVariable String externalId) {
        CountryDTO country = countryService.getCountryByExternalId(externalId);
        return ResponseEntity.ok(country);
    }
    
    /**
     * Create a new country.
     * POST /api/countries
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create country", description = "Create a new country")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO countryDTO) {
        CountryDTO createdCountry = countryService.createCountry(countryDTO);
        return new ResponseEntity<>(createdCountry, HttpStatus.CREATED);
    }
    
    /**
     * Update country by ID.
     * PUT /api/countries/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update country", description = "Update an existing country")
    public ResponseEntity<CountryDTO> updateCountry(
            @PathVariable Long id,
            @Valid @RequestBody CountryDTO countryDTO) {
        CountryDTO updatedCountry = countryService.updateCountry(id, countryDTO);
        return ResponseEntity.ok(updatedCountry);
    }
    
    /**
     * Delete country by ID.
     * DELETE /api/countries/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete country", description = "Delete a country by its ID")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}

