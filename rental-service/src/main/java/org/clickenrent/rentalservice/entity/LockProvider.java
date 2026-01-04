package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing lock providers/manufacturers.
 * Examples: AXA, OMNI, Generic BLE
 */
@Entity
@Table(
    name = "lock_provider",
    indexes = {
        @Index(name = "idx_lock_provider_name", columnList = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class LockProvider extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Lock provider name is required")
    @Size(max = 100, message = "Lock provider name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500, message = "API endpoint must not exceed 500 characters")
    @Column(name = "api_endpoint", length = 500)
    private String apiEndpoint;

    @Size(max = 500, message = "API key must not exceed 500 characters")
    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Size(max = 500, message = "Encryption key must not exceed 500 characters")
    @Column(name = "encryption_key", length = 500)
    private String encryptionKey;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}








