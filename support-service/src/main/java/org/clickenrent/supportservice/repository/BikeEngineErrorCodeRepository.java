package org.clickenrent.supportservice.repository;

import org.clickenrent.supportservice.entity.BikeEngineErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BikeEngineErrorCode junction entity.
 */
@Repository
public interface BikeEngineErrorCodeRepository extends JpaRepository<BikeEngineErrorCode, Long> {
    
    Optional<BikeEngineErrorCode> findByExternalId(String externalId);
    
    List<BikeEngineErrorCode> findByBikeEngineExternalId(String bikeEngineExternalId);
    
    List<BikeEngineErrorCode> findByErrorCodeId(Long errorCodeId);
    
    Optional<BikeEngineErrorCode> findByBikeEngineExternalIdAndErrorCodeId(String bikeEngineExternalId, Long errorCodeId);
}
