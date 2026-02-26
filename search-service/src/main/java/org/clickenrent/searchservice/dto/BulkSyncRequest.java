package org.clickenrent.searchservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for bulk synchronization of entities from source services.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkSyncRequest {

    /**
     * Types of entities to sync (users, bikes, locations, hubs)
     */
    @NotEmpty(message = "At least one entity type is required")
    private List<String> entityTypes;

    /**
     * Optional company external ID to filter results (null = all companies)
     */
    private String companyExternalId;
}
