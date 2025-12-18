package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing part categories with hierarchical structure.
 * Self-referencing for parent-child relationships.
 */
@Entity
@Table(
    name = "part_category",
    indexes = {
        @Index(name = "idx_part_category_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"parentCategory"})
@EqualsAndHashCode(of = "id")
public class PartCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Part category name is required")
    @Size(max = 100, message = "Part category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private PartCategory parentCategory;
}

