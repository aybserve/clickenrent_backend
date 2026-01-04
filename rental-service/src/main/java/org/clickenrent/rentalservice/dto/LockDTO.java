package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LockStatusDTO lockStatus;
    private LockProviderDTO lockProvider;
    private Integer batteryLevel;
    private LocalDateTime lastSeenAt;
    private String firmwareVersion;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
