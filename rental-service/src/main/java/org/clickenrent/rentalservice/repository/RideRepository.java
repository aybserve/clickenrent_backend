package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ride entity.
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByExternalId(String externalId);
    List<Ride> findByBikeRental(BikeRental bikeRental);
}








