package org.clickenrent.rentalservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for geocoding (address to coordinates).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingRequestDTO {

    @NotBlank(message = "Address is required")
    private String address;

    private String country;
    private String language;
}

