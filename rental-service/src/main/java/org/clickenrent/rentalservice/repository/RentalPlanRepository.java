package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.RentalPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RentalPlan entity.
 */
@Repository
public interface RentalPlanRepository extends JpaRepository<RentalPlan, Long> {
    List<RentalPlan> findByLocation(Location location);
    Optional<RentalPlan> findByExternalId(String externalId);
}








