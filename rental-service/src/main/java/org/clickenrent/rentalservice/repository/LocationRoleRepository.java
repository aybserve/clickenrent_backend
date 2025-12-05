package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.LocationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for LocationRole entity.
 */
@Repository
public interface LocationRoleRepository extends JpaRepository<LocationRole, Long> {
    Optional<LocationRole> findByName(String name);
}
