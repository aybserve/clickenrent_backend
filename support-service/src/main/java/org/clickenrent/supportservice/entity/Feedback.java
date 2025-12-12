package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing general user feedback with ratings and comments.
 */
@Entity
@Table(
    name = "feedback",
    indexes = {
        @Index(name = "idx_feedback_external_id", columnList = "external_id"),
        @Index(name = "idx_feedback_user_id", columnList = "user_id")
    }
)
@SQLDelete(sql = "UPDATE feedback SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Feedback extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Rate is required")
    @Min(value = 1, message = "Rate must be at least 1")
    @Max(value = 5, message = "Rate must not exceed 5")
    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    @Column(name = "comment", length = 2000)
    private String comment;

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}
