package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rental duration statistics.
 * Contains min, max, and average duration values.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDurationDTO {

    private Integer average;
    private Integer min;
    private Integer max;
    private String unit;
}
