package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing the relationship between BikeModel and Part.
 * Join table for many-to-many relationship.
 */
@Entity
@Table(
        name = "bike_model_part",
        indexes = {
                @Index(name = "idx_bike_model_part_bike_model_id", columnList = "bike_model_id"),
                @Index(name = "idx_bike_model_part_part_id", columnList = "part_id")
        }
)
@SQLDelete(sql = "UPDATE bike_model_part SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeModelPart extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Bike model is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

    @NotNull(message = "Part is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

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





