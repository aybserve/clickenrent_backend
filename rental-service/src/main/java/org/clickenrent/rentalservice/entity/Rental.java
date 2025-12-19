package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Entity representing a rental order.
 */
@Entity
@Table(
    name = "rental",
    indexes = {
        @Index(name = "idx_rental_external_id", columnList = "external_id"),
        @Index(name = "idx_rental_user_id", columnList = "user_id"),
        @Index(name = "idx_rental_company_id", columnList = "company_id"),
        @Index(name = "idx_rental_user_external_id", columnList = "user_external_id"),
        @Index(name = "idx_rental_company_external_id", columnList = "company_external_id"),
        @Index(name = "idx_rental_erp_order_id", columnList = "erp_rental_order_id")
    }
)
@SQLDelete(sql = "UPDATE rental SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Rental extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Company ID is required")
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // Cross-service reference fields using externalId
    @Column(name = "user_external_id", length = 100)
    private String userExternalId;

    @Column(name = "company_external_id", length = 100)
    private String companyExternalId;

    @NotNull(message = "Rental status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_status_id", nullable = false)
    private RentalStatus rentalStatus;

    @Size(max = 100, message = "ERP rental order ID must not exceed 100 characters")
    @Column(name = "erp_rental_order_id", length = 100)
    private String erpRentalOrderId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
