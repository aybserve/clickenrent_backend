package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CompanyRole entity.
 * Represents roles within a company (e.g., Owner, Admin, Staff).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRoleDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private String name;
}










