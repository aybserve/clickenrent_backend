package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.entity.BikeModelRentalPlan;
import org.clickenrent.rentalservice.entity.RentalPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BikeModelRentalPlan entity.
 */
@Repository
public interface BikeModelRentalPlanRepository extends JpaRepository<BikeModelRentalPlan, Long> {
    List<BikeModelRentalPlan> findByBikeModel(BikeModel bikeModel);
    List<BikeModelRentalPlan> findByRentalPlan(RentalPlan rentalPlan);
}


