package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Junction entity linking bike engines to error codes.
 */
@Entity
@Table(
    name = "bike_engine_error_code",
    indexes = {
        @Index(name = "idx_bee_code_error_code", columnList = "error_code_id"),
        @Index(name = "idx_bee_code_bike_engine_ext_id", columnList = "bike_engine_external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeEngineErrorCode extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @Column(name = "bike_engine_external_id", length = 100)
    private String bikeEngineExternalId;

    @NotNull(message = "Error code is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_code_id", nullable = false)
    private ErrorCode errorCode;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
