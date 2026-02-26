package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserLocation entity.
 */
@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    List<UserLocation> findByUserExternalId(String userExternalId);
    List<UserLocation> findByLocation(Location location);
    Optional<UserLocation> findByExternalId(String externalId);
}




