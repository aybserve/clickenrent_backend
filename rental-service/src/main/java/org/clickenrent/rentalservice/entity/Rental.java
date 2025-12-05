package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
        @Index(name = "idx_rental_erp_order_id", columnList = "erp_rental_order_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

    @NotNull(message = "Rental status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_status_id", nullable = false)
    private RentalStatus rentalStatus;

    @Size(max = 100, message = "ERP rental order ID must not exceed 100 characters")
    @Column(name = "erp_rental_order_id", length = 100)
    private String erpRentalOrderId;
}
