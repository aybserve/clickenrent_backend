package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rental summary statistics.
 * Contains aggregated counts and cancellation rate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalSummaryDTO {

    private Integer totalBikeRentals;
    private Integer completedBikeRentals;
    private Integer cancelledBikeRentals;
    private Double cancellationRate;
}
