package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Placeholder DTO for B2B Sale (fetched from rental-service).
 * TODO: Sync with actual B2BSale structure from rental-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleDTO {
    private Long id;
    private String externalId;
}








