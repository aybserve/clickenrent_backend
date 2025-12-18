package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Junction entity linking bike types to their common issues.
 */
@Entity
@Table(
    name = "bike_type_bike_issue",
    indexes = {
        @Index(name = "idx_bike_type_bike_issue_bike_type", columnList = "bike_type_id"),
        @Index(name = "idx_bike_type_bike_issue_bike_issue", columnList = "bike_issue_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeTypeBikeIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bike type ID is required")
    @Column(name = "bike_type_id", nullable = false)
    private Long bikeTypeId;

    @NotNull(message = "Bike issue is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_issue_id", nullable = false)
    private BikeIssue bikeIssue;
}

