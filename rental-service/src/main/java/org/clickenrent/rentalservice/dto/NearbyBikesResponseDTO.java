package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for nearby bikes search.
 * Contains list of bikes and total count.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyBikesResponseDTO {

    private List<BikeLocationDTO> bikes;
    private Long total;
}

