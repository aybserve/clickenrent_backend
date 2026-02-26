package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing items in a B2B sale.
 */
@Entity
@Table(
    name = "b2b_sale_item",
    indexes = {
        @Index(name = "idx_b2b_sale_item_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_item_product_id", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSaleItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "B2B sale is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_id", nullable = false)
    private B2BSale b2bSale;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Price is required")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @NotNull(message = "Total price is required")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}

