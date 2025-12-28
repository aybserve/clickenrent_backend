package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Placeholder DTO for B2B Subscription (fetched from rental-service).
 * TODO: Sync with actual B2BSubscription structure from rental-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionDTO {
    private Long id;
    private String externalId;
}








