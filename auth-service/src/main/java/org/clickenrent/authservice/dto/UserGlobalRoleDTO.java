package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for UserGlobalRole entity.
 * Represents the relationship between a user and their global system role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGlobalRoleDTO {

    private Long id;
    private Long userId;
    private Long globalRoleId;
}


