package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing B2B sales between companies.
 */
@Entity
@Table(
    name = "b2b_sale",
    indexes = {
        @Index(name = "idx_b2b_sale_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_seller_company_id", columnList = "seller_company_id"),
        @Index(name = "idx_b2b_sale_buyer_company_id", columnList = "buyer_company_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class B2BSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Seller company ID is required")
    @Column(name = "seller_company_id", nullable = false)
    private Long sellerCompanyId;

    @NotNull(message = "Buyer company ID is required")
    @Column(name = "buyer_company_id", nullable = false)
    private Long buyerCompanyId;

    @NotNull(message = "B2B sale status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_status_id", nullable = false)
    private B2BSaleStatus b2bSaleStatus;

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}
