package org.clickenrent.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning a global role to a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignGlobalRoleRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Global Role ID is required")
    private Long globalRoleId;
}

