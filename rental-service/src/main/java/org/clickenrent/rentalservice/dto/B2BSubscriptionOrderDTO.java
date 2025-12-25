package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSubscriptionOrder entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionOrderDTO {

    private Long id;
    private String externalId;
    private Long locationId;
    private LocalDateTime dateTime;
    private Long b2bSubscriptionOrderStatusId;
    private Long b2bSubscriptionId;
}






