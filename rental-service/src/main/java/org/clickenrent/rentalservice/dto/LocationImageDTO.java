package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for LocationImage entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationImageDTO {

    private Long id;
    private String externalId;
    private Long locationId;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean isThumbnail;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

