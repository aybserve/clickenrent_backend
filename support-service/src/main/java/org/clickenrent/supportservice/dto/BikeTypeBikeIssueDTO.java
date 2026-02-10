package org.clickenrent.supportservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BikeTypeBikeIssue junction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeTypeBikeIssueDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private String bikeTypeExternalId;
    private Long bikeIssueId;
    private String bikeIssueName;
}








