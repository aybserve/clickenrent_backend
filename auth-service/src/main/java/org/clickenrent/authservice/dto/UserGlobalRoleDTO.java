package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for UserGlobalRole entity.
 * Represents the relationship between a user and their global system role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGlobalRoleDTO {

    private Long id;
    private Long userId;
    private Long globalRoleId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}


