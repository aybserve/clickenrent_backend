package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikeReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BikeReservation entity.
 */
@Repository
public interface BikeReservationRepository extends JpaRepository<BikeReservation, Long> {
    Optional<BikeReservation> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    List<BikeReservation> findByUserId(Long userId);
    List<BikeReservation> findByBike(Bike bike);
}


