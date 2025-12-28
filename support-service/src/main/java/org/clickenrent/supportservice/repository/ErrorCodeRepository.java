package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ErrorCode entity.
 */
@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Long> {
    
    Optional<ErrorCode> findByExternalId(String externalId);
    
    List<ErrorCode> findByBikeEngineExternalId(String bikeEngineExternalId);
    
    List<ErrorCode> findByName(String name);
}








