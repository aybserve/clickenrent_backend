package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing a physical address.
 * Contains street, postcode, city, and country information.
 */
@Entity
@Table(
    name = "address",
    indexes = {
        @Index(name = "idx_address_country", columnList = "country_id")
    }
)
@SQLDelete(sql = "UPDATE address SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Address extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotNull(message = "Country is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Size(max = 255, message = "Street must not exceed 255 characters")
    @Column(name = "street", length = 255)
    private String street;

    @Size(max = 20, message = "Postcode must not exceed 20 characters")
    @Column(name = "postcode", length = 20)
    private String postcode;
}

