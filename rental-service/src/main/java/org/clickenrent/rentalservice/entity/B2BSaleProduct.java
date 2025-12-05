package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing products in a B2B sale.
 */
@Entity
@Table(
    name = "b2b_sale_product",
    indexes = {
        @Index(name = "idx_b2b_sale_product_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_product_product_id", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class B2BSaleProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "B2B sale is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_id", nullable = false)
    private B2BSale b2bSale;

    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Price is required")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
