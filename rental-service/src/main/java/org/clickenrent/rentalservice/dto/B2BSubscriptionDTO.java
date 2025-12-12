package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSubscription entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionDTO {

    private Long id;
    private String externalId;
    private Long locationId;
    private LocalDateTime endDateTime;
    private Long b2bSubscriptionStatusId;
}
