package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Junction entity linking bike types to their common issues.
 */
@Entity
@Table(
    name = "bike_type_bike_issue",
    indexes = {
        @Index(name = "idx_bike_type_bike_issue_bike_issue", columnList = "bike_issue_id"),
        @Index(name = "idx_bike_type_bike_issue_bike_type_ext_id", columnList = "bike_type_external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeTypeBikeIssue extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @Column(name = "bike_type_external_id", length = 100)
    private String bikeTypeExternalId;

    @NotNull(message = "Bike issue is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_issue_id", nullable = false)
    private BikeIssue bikeIssue;

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








