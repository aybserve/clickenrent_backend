package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSaleOrderStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleOrderStatusDTO {

    private Long id;
    private String externalId;
    private String name;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








