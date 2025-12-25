package org.clickenrent.rentalservice.repository;

import org.clickenrent.rentalservice.entity.B2BSaleOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for B2BSaleOrderStatus entity.
 */
@Repository
public interface B2BSaleOrderStatusRepository extends JpaRepository<B2BSaleOrderStatus, Long> {
    Optional<B2BSaleOrderStatus> findByName(String name);
}






