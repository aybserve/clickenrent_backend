package org.clickenrent.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning a user to a company with a specific role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserToCompanyRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Company Role ID is required")
    private Long companyRoleId;
}


