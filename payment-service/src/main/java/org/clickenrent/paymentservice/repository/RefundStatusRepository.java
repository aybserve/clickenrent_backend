package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RefundStatus entity
 */
@Repository
public interface RefundStatusRepository extends JpaRepository<RefundStatus, Long> {
    
    Optional<RefundStatus> findByCode(String code);
}
