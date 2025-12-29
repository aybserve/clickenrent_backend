package org.clickenrent.rentalservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for reverse geocoding (coordinates to address).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReverseGeocodingRequestDTO {

    @NotNull(message = "Location is required")
    @Valid
    private GeoPointDTO location;

    private String language;
}

