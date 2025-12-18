package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Address entity.
 * Provides standard CRUD operations for managing addresses.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByCityId(Long cityId);
}


