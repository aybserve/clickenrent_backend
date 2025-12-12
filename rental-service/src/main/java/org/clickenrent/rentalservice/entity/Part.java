package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing a part (extends Product).
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("PART")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Part extends Product implements ProductModelType {

    @Column(name = "vat", precision = 5, scale = 2)
    private BigDecimal vat;

    @Builder.Default
    @Column(name = "is_vat_include", nullable = true)
    private Boolean isVatInclude = false;

    @NotBlank(message = "Part name is required")
    @Size(max = 100, message = "Part name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Part brand is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_brand_id", nullable = false)
    private PartBrand partBrand;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull(message = "Part category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_category_id", nullable = false)
    private PartCategory partCategory;

    @NotNull(message = "Hub is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id", nullable = false)
    private Hub hub;

    @NotNull(message = "B2B sale price is required")
    @Column(name = "b2b_sale_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal b2bSalePrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Override
    public String getProductModelTypeName() {
        return "PART";
    }
}
