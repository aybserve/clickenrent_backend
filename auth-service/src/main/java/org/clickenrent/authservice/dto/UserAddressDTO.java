package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for UserAddress entity.
 * Represents the link between a user and an address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {

    private Long id;
    private Long userId;
    private Long addressId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

