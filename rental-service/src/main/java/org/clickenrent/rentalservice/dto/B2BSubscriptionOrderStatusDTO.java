package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for B2BSubscriptionOrderStatus entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionOrderStatusDTO {

    private Long id;
    private String name;
}
