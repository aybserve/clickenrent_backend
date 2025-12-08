package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSubscriptionItem entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionItemDTO {

    private Long id;
    private String externalId;
    private Long b2bSubscriptionId;
    private Long productId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
