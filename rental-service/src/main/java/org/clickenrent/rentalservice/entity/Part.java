package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class Part extends Product {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private Hub hub;

//    @NotNull(message = "Part model is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_model_id", nullable = true)
    private PartModel partModel;
}
