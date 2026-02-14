package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bike type breakdown statistics.
 * Contains bike type and count of rentals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeTypeBreakdownDTO {

    private String type;
    private Integer count;
}
