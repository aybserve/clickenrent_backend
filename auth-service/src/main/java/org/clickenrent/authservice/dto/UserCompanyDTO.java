package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for UserCompany entity.
 * Represents the relationship between a user and a company with their respective role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyDTO {

    private Long id;
    private Long userId;
    private Long companyId;
    private Long companyRoleId;
}


