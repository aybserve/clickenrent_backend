package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for RideStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideStatusDTO {

    private Long id;
    private String name;
}








