package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationBankAccountRepository extends JpaRepository<LocationBankAccount, Long> {
    
    Optional<LocationBankAccount> findByExternalId(String externalId);
    
    Optional<LocationBankAccount> findByLocationExternalId(String locationExternalId);
    
    List<LocationBankAccount> findByLocationExternalIdAndIsActiveTrue(String locationExternalId);
    
    List<LocationBankAccount> findByCompanyExternalId(String companyExternalId);
    
    boolean existsByLocationExternalId(String locationExternalId);
}
