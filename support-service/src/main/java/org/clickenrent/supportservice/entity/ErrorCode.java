package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing diagnostic error codes linked to bike engines.
 */
@Entity
@Table(
    name = "error_code",
    indexes = {
        @Index(name = "idx_error_code_external_id", columnList = "external_id"),
        @Index(name = "idx_error_code_bike_engine_ext_id", columnList = "bike_engine_external_id")
    }
)
@SQLDelete(sql = "UPDATE error_code SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ErrorCode extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "bike_engine_external_id", length = 100)
    private String bikeEngineExternalId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 1000, message = "Common cause must not exceed 1000 characters")
    @Column(name = "common_cause", length = 1000)
    private String commonCause;

    @Size(max = 2000, message = "Diagnostic steps must not exceed 2000 characters")
    @Column(name = "diagnostic_steps", length = 2000)
    private String diagnosticSteps;

    @Size(max = 1000, message = "Recommended fix must not exceed 1000 characters")
    @Column(name = "recommended_fix", length = 1000)
    private String recommendedFix;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Column(name = "notes", length = 2000)
    private String notes;

    @Builder.Default
    @Column(name = "is_fixable_by_client", nullable = false)
    private Boolean isFixableByClient = false;
}




