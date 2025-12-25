package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.LocationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LocationImage entity.
 */
@Repository
public interface LocationImageRepository extends JpaRepository<LocationImage, Long> {
    Optional<LocationImage> findByExternalId(String externalId);
    List<LocationImage> findByLocation(Location location);
}






