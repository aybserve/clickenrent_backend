package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ResponsiblePerson entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsiblePersonDTO {

    private Long id;
    private String name;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

