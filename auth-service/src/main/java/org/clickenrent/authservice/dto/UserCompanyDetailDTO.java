package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced Data Transfer Object for UserCompany entity with nested details.
 * Used for GET operations where full information is needed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyDetailDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private UserDTO user;
    private CompanyDTO company;
    private CompanyRoleDTO companyRole;
}










