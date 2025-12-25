package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for RentalStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalStatusDTO {

    private Long id;
    private String name;
}






