package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing step-by-step troubleshooting guide items.
 */
@Entity
@Table(
    name = "support_request_guide_item",
    indexes = {
        @Index(name = "idx_support_request_guide_item_bike_issue", columnList = "bike_issue_id"),
        @Index(name = "idx_support_request_guide_item_status", columnList = "support_request_status_id")
    }
)
@SQLDelete(sql = "UPDATE support_request_guide_item SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class SupportRequestGuideItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Index is required")
    @Column(name = "item_index", nullable = false)
    private Integer itemIndex;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @NotNull(message = "Bike issue is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_issue_id", nullable = false)
    private BikeIssue bikeIssue;

    @NotNull(message = "Support request status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_request_status_id", nullable = false)
    private SupportRequestStatus supportRequestStatus;
}

