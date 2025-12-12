package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing B2B sales between companies.
 */
@Entity
@Table(
    name = "b2b_sale",
    indexes = {
        @Index(name = "idx_b2b_sale_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_location_id", columnList = "location_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_sale SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSale extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "B2B sale status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_status_id", nullable = false)
    private B2BSaleStatus b2bSaleStatus;

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}
