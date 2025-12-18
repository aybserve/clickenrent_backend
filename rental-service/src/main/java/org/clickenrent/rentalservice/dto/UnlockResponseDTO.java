package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for unlock operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnlockResponseDTO {

    private String unlockToken;
    private String lockId;
    private Integer expiresIn;
    private String algorithm;
}


