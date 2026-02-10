package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entity representing bike issues with hierarchical structure.
 * Can have parent issues for categorization.
 */
@Entity
@Table(
    name = "bike_issue",
    indexes = {
        @Index(name = "idx_bike_issue_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_issue_erp_external_id", columnList = "erp_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_issue SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeIssue extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "erp_external_id", unique = true, length = 100)
    private String erpExternalId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_bike_issue_id")
    private BikeIssue parentBikeIssue;

    @Builder.Default
    @Column(name = "is_fixable_by_client", nullable = false)
    private Boolean isFixableByClient = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_person_id")
    private ResponsiblePerson responsiblePerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_unit_id")
    private BikeUnit bikeUnit;

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








