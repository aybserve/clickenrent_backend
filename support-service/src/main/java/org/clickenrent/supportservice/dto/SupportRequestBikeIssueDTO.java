package org.clickenrent.supportservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for SupportRequestBikeIssue junction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestBikeIssueDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private Long supportRequestId;
    private String supportRequestExternalId;
    private Long bikeIssueId;
    private String bikeIssueName;
}








