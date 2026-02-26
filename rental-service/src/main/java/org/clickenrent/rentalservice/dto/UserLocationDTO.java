package org.clickenrent.rentalservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for UserLocation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private Long locationId;
    private Long locationRoleId;

    // Cross-service externalId reference
    private String userExternalId;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private java.time.LocalDateTime dateCreated;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private java.time.LocalDateTime lastDateModified;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}




