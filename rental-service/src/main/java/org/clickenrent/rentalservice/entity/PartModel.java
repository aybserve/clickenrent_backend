package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing part models.
 */
@Entity
@Table(
    name = "part_model",
    indexes = {
        @Index(name = "idx_part_model_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class PartModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Part model name is required")
    @Size(max = 100, message = "Part model name must not exceed 100 characters")
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
}
