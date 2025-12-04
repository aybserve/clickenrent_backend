package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for UserAddress entity.
 * Represents the link between a user and an address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {

    private Long id;
    private Long userId;
    private Long addressId;
}

