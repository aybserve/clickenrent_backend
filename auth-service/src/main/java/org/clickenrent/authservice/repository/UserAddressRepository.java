package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for UserAddress entity.
 * Provides standard CRUD operations for managing user-address relationships.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    
    List<UserAddress> findByUserId(Long userId);
    
    List<UserAddress> findByAddressId(Long addressId);
}








