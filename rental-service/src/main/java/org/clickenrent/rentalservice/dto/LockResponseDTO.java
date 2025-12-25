package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for lock operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockResponseDTO {

    private Boolean success;
    private String rentalStatus;
}






