package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for peak hour rental statistics.
 * Contains hour (0-23) and number of rentals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakHourDTO {

    private Integer hour;
    private Integer bikeRentals;
}
