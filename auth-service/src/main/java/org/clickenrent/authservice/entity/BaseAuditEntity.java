package org.clickenrent.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class for audit fields.
 * All entities that need audit tracking should extend this class.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity {

    @CreatedDate
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(name = "last_date_modified")
    private LocalDateTime lastDateModified;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * Abstract methods that must be implemented by concrete entities.
     * These allow the base class to manage server-controlled fields.
     */
    public abstract Long getId();
    public abstract void setId(Long id);
    public abstract String getExternalId();
    public abstract void setExternalId(String externalId);

    /**
     * Sanitizes entity for creation by nullifying all server-managed fields.
     * This prevents clients from controlling IDs, externalIds, or audit fields.
     * Must be called in service layer before save() for create operations.
     */
    public void sanitizeForCreate() {
        setId(null);
        setExternalId(null);
        setDateCreated(null);
        setLastDateModified(null);
        setCreatedBy(null);
        setLastModifiedBy(null);
    }

    /**
     * Ensures default values are set before persistence.
     * Generates UUID for externalId if not already set.
     */
    @PrePersist
    protected void ensureDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (getExternalId() == null || getExternalId().isEmpty()) {
            setExternalId(UUID.randomUUID().toString());
        }
    }
}


