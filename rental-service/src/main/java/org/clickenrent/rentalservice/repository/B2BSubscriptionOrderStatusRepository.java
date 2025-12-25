package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for B2BSubscriptionOrderStatus entity.
 */
@Repository
public interface B2BSubscriptionOrderStatusRepository extends JpaRepository<B2BSubscriptionOrderStatus, Long> {
    Optional<B2BSubscriptionOrderStatus> findByName(String name);
}






