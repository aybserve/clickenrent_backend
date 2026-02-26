package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for Location entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: support-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    private Long id;
    private String externalId;
    private String erpPartnerId;
    private String name;
    private String address;
    private String description;
    private String companyExternalId;
    private Boolean isPublic;
    private Boolean isActive;
    private String directions;
    private Long coordinatesId;
    private String thumbnailImageUrl;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}






