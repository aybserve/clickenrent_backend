package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for peak day rental statistics.
 * Contains day of week and number of rentals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakDayDTO {

    private String dayOfWeek;
    private Integer bikeRentals;
}
