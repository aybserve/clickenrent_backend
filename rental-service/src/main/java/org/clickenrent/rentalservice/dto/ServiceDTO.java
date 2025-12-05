package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Service entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {

    private Long id;
    private String name;
}
