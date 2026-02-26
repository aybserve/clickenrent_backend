package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for B2BSubscription entity.
 */
@Repository
public interface B2BSubscriptionRepository extends JpaRepository<B2BSubscription, Long> {
    Optional<B2BSubscription> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    List<B2BSubscription> findByLocationId(Long locationId);
}
