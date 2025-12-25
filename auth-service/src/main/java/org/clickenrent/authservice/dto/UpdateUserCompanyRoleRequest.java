package org.clickenrent.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a user's role in a company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCompanyRoleRequest {

    @NotNull(message = "Company Role ID is required")
    private Long companyRoleId;
}








