package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeReservationDTO;
import org.clickenrent.rentalservice.service.BikeReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike-reservations")
@RequiredArgsConstructor
@Tag(name = "BikeReservation", description = "Bike reservation management")
@SecurityRequirement(name = "bearerAuth")
public class BikeReservationController {

    private final BikeReservationService bikeReservationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all bike reservations")
    public ResponseEntity<Page<BikeReservationDTO>> getAllReservations(
            @PageableDefault(size = 20, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bikeReservationService.getAllReservations(pageable));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get reservations by user")
    public ResponseEntity<List<BikeReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bikeReservationService.getReservationsByUser(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<BikeReservationDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeReservationService.getReservationById(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create bike reservation", description = "Users can only reserve bikes for themselves")
    public ResponseEntity<BikeReservationDTO> createReservation(@Valid @RequestBody BikeReservationDTO dto) {
        return new ResponseEntity<>(bikeReservationService.createReservation(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel reservation", description = "Users can only cancel their own reservations")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        bikeReservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}


