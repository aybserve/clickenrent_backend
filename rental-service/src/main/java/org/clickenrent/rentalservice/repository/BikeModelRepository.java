package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for BikeModel entity.
 */
@Repository
public interface BikeModelRepository extends JpaRepository<BikeModel, Long> {
    Optional<BikeModel> findByExternalId(String externalId);
}


