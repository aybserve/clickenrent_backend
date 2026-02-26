package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CompanyType entity.
 * Represents types of companies (e.g., Hotel, B&B, Camping).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyTypeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private String name;
}










