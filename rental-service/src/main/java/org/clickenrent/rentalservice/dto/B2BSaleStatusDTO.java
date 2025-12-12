package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for B2BSaleStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleStatusDTO {

    private Long id;
    private String name;
}
