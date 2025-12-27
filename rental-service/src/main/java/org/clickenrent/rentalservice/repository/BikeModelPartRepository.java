package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeModelPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BikeModelPart entity.
 */
@Repository
public interface BikeModelPartRepository extends JpaRepository<BikeModelPart, Long> {
    List<BikeModelPart> findByBikeModelId(Long bikeModelId);
    List<BikeModelPart> findByPartId(Long partId);
}




