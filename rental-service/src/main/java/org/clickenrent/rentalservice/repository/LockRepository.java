package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Lock entity.
 */
@Repository
public interface LockRepository extends JpaRepository<Lock, Long> {
    Optional<Lock> findByExternalId(String externalId);
    Optional<Lock> findByMacAddress(String macAddress);
}






