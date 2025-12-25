package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.ChargingStationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ChargingStationModel entity.
 */
@Repository
public interface ChargingStationModelRepository extends JpaRepository<ChargingStationModel, Long> {
    Optional<ChargingStationModel> findByExternalId(String externalId);
}






