package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing part types.
 * Examples: With serial numbers, Without serial numbers
 */
@Entity
@Table(name = "part_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class PartType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Part type name is required")
    @Size(max = 100, message = "Part type name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
}
