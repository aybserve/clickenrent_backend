package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.Refund;
import org.clickenrent.paymentservice.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Refund entity
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Optional<Refund> findByExternalId(String externalId);
    
    List<Refund> findByFinancialTransactionId(Long financialTransactionId);
    
    List<Refund> findByRefundStatus(RefundStatus refundStatus);
    
    List<Refund> findByCompanyExternalId(String companyExternalId);
    
    // Find refunds with pagination ordered by creation date
    Page<Refund> findByCompanyExternalIdOrderByDateCreatedDesc(
        String companyExternalId, Pageable pageable
    );
}
