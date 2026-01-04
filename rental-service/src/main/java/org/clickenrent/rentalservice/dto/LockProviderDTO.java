package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for LockProvider entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockProviderDTO {

    private Long id;
    private String externalId;
    private String name;
    private String apiEndpoint;
    private String apiKey;
    private String encryptionKey;
    private Boolean isActive;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








