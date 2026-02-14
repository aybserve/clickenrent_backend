package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for B2BSubscriptionStatus entity.
 */
@Repository
public interface B2BSubscriptionStatusRepository extends JpaRepository<B2BSubscriptionStatus, Long> {
    Optional<B2BSubscriptionStatus> findByName(String name);
    Optional<B2BSubscriptionStatus> findByExternalId(String externalId);
}








