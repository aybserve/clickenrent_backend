package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Key entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyDTO {

    private Long id;
    private String externalId;
    private Long lockId;
}






