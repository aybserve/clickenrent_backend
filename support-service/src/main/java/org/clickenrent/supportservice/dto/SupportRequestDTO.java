package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SupportRequest entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestDTO {

    private Long id;
    private String externalId;
    private Long userId;
    private Long bikeId;
    private Boolean isNearLocation;
    private String photoUrl;
    private Long errorCodeId;
    private String errorCodeName;
    private Long supportRequestStatusId;
    private String supportRequestStatusName;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
