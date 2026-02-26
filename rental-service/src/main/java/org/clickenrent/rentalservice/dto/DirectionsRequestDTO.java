package org.clickenrent.rentalservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for directions/routing between two points.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectionsRequestDTO {

    @NotNull(message = "Origin location is required")
    @Valid
    private GeoPointDTO origin;

    @NotNull(message = "Destination location is required")
    @Valid
    private GeoPointDTO destination;

    private String profile; // driving, walking, cycling
    private Boolean alternatives;
    private Boolean steps;
}

