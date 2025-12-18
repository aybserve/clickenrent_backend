package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing customer support requests.
 */
@Entity
@Table(
    name = "support_request",
    indexes = {
        @Index(name = "idx_support_request_external_id", columnList = "external_id"),
        @Index(name = "idx_support_request_user_id", columnList = "user_id"),
        @Index(name = "idx_support_request_bike_id", columnList = "bike_id")
    }
)
@SQLDelete(sql = "UPDATE support_request SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class SupportRequest extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bike_id")
    private Long bikeId;

    @Builder.Default
    @Column(name = "is_near_location", nullable = false)
    private Boolean isNearLocation = false;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_code_id")
    private ErrorCode errorCode;

    @NotNull(message = "Support request status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_request_status_id", nullable = false)
    private SupportRequestStatus supportRequestStatus;
}

