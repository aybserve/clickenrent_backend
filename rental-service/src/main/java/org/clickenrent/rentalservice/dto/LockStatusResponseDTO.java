package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for lock status query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockStatusResponseDTO {

    private String lockId;
    private String status;
    private Integer batteryLevel;
    private LocalDateTime lastSeen;
}


