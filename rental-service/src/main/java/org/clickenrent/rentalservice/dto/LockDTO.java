package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Lock entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockDTO {

    private Long id;
    private String externalId;
    private String macAddress;
}
