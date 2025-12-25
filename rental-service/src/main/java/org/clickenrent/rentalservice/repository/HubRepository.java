package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Hub entity.
 */
@Repository
public interface HubRepository extends JpaRepository<Hub, Long> {
    Optional<Hub> findByExternalId(String externalId);
    List<Hub> findByLocation(Location location);
}






