package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.LockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockStatusRepository extends JpaRepository<LockStatus, Long> {
    Optional<LockStatus> findByName(String name);
    Optional<LockStatus> findByExternalId(String externalId);
}








