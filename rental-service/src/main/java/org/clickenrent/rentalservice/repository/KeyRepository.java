package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.Key;
import org.clickenrent.rentalservice.entity.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Key entity.
 */
@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {
    Optional<Key> findByExternalId(String externalId);
    List<Key> findByLock(Lock lock);
}






