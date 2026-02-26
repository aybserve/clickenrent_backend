package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for B2BRevenueSharePayout entity
 */
@Repository
public interface B2BRevenueSharePayoutRepository extends JpaRepository<B2BRevenueSharePayout, Long> {
    
    List<B2BRevenueSharePayout> findByCompanyExternalId(String companyExternalId);
    
    List<B2BRevenueSharePayout> findByPaymentStatus(PaymentStatus status);
    
    Optional<B2BRevenueSharePayout> findByExternalId(String externalId);
    
    Optional<B2BRevenueSharePayout> findByMultiSafepayPayoutId(String multiSafepayPayoutId);
}




