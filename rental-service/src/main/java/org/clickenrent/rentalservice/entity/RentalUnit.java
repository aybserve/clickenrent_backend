package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing rental units.
 * Examples: Day, Hour, Week
 */
@Entity
@Table(name = "rental_unit")
@SQLDelete(sql = "UPDATE rental_unit SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class RentalUnit extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rental unit name is required")
    @Size(max = 50, message = "Rental unit name must not exceed 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}
