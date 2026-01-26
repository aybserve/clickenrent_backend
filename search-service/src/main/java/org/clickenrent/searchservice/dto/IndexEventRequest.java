package org.clickenrent.searchservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for event-driven indexing operations.
 * Other services send this when entities are created/updated/deleted.
 * 
 * @author Vitaliy Shvetsov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexEventRequest {

    /**
     * Type of entity (user, bike, location, hub)
     */
    @NotBlank(message = "Entity type is required")
    @Pattern(regexp = "^(user|bike|location|hub)$", message = "Invalid entity type")
    private String entityType;

    /**
     * External ID of the entity
     */
    @NotBlank(message = "Entity ID is required")
    private String entityId;

    /**
     * Operation type (CREATE, UPDATE, DELETE)
     */
    @NotNull(message = "Operation is required")
    private IndexOperation operation;

    public enum IndexOperation {
        CREATE,
        UPDATE,
        DELETE
    }
}
