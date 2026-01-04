package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Service entity.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByExternalId(String externalId);
}








