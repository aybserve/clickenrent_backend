package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Junction entity linking support requests to multiple bike issues.
 */
@Entity
@Table(
    name = "support_request_bike_issue",
    indexes = {
        @Index(name = "idx_support_request_bike_issue_request", columnList = "support_request_id"),
        @Index(name = "idx_support_request_bike_issue_issue", columnList = "bike_issue_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class SupportRequestBikeIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @NotNull(message = "Support request is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_request_id", nullable = false)
    private SupportRequest supportRequest;

    @NotNull(message = "Bike issue is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_issue_id", nullable = false)
    private BikeIssue bikeIssue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @PrePersist
    protected void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}








