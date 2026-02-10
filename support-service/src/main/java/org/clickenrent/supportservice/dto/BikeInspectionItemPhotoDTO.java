package org.clickenrent.supportservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeInspectionItemPhoto entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeInspectionItemPhotoDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Long bikeInspectionItemId;
    private String photoUrl;
    private String companyExternalId;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
