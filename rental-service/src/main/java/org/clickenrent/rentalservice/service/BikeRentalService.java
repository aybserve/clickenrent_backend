package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.dto.*;
import org.clickenrent.rentalservice.entity.*;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeRentalMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.LockRepository;
import org.clickenrent.rentalservice.repository.RentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeRentalService {

    private final BikeRentalRepository bikeRentalRepository;
    private final BikeRepository bikeRepository;
    private final RentalRepository rentalRepository;
    private final LockRepository lockRepository;
    private final BikeRentalMapper bikeRentalMapper;
    private final SecurityService securityService;
    private final LockEncryptionService lockEncryptionService;
    private final LockStatusService lockStatusService;
    private final CoordinatesService coordinatesService;

    @Transactional(readOnly = true)
    public Page<BikeRentalDTO> getAllBikeRentals(Pageable pageable) {
        if (securityService.isAdmin()) {
            return bikeRentalRepository.findAll(pageable)
                    .map(bikeRentalMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all bike rentals");
    }

    @Transactional(readOnly = true)
    public BikeRentalDTO getBikeRentalById(Long id) {
        BikeRental bikeRental = bikeRentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", id));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this bike rental");
        }

        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional(readOnly = true)
    public BikeRentalDTO findByExternalId(String externalId) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        // Security check - verify access through rental
        if (bikeRental.getRental() != null) {
            if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to view this bike rental");
            }
        }
        
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public BikeRentalDTO updateByExternalId(String externalId, BikeRentalDTO dto) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        // Security check
        if (bikeRental.getRental() != null) {
            if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to update this bike rental");
            }
        }
        
        // Update fields
        if (dto.getStartDateTime() != null) bikeRental.setStartDateTime(dto.getStartDateTime());
        if (dto.getEndDateTime() != null) bikeRental.setEndDateTime(dto.getEndDateTime());
        if (dto.getPrice() != null) bikeRental.setPrice(dto.getPrice());
        if (dto.getTotalPrice() != null) bikeRental.setTotalPrice(dto.getTotalPrice());
        
        bikeRental = bikeRentalRepository.save(bikeRental);
        log.info("Updated bike rental by externalId: {}", externalId);
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete bike rentals");
        }
        
        bikeRentalRepository.delete(bikeRental);
        log.info("Deleted bike rental by externalId: {}", externalId);
    }

    @Transactional
    public BikeRentalDTO createBikeRental(BikeRentalDTO dto) {
        // Validate bike and rental exist
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", dto.getBikeId()));
        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", dto.getRentalId()));

        // Check permissions
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(rental.getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to create bike rentals");
        }

        BikeRental bikeRental = bikeRentalMapper.toEntity(dto);
        
        // DUAL-WRITE: Populate cross-service externalId reference fields
        if (bike.getExternalId() != null) {
            bikeRental.setBikeExternalId(bike.getExternalId());
            log.debug("Populated bikeExternalId: {} for bike rental", bike.getExternalId());
        }
        
        if (bikeRental.getLocation() != null && bikeRental.getLocation().getExternalId() != null) {
            bikeRental.setLocationExternalId(bikeRental.getLocation().getExternalId());
            log.debug("Populated locationExternalId: {} for bike rental", bikeRental.getLocation().getExternalId());
        }
        
        if (rental.getExternalId() != null) {
            bikeRental.setRentalExternalId(rental.getExternalId());
            log.debug("Populated rentalExternalId: {} for bike rental", rental.getExternalId());
        }
        
        // Calculate revenue share if B2B rentable
        if (bike.getIsB2BRentable() && bike.getRevenueSharePercent() != null) {
            bikeRental.setIsRevenueSharePaid(false);
        }

        bikeRental = bikeRentalRepository.save(bikeRental);
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public void deleteBikeRental(Long id) {
        BikeRental bikeRental = bikeRentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike rentals");
        }

        bikeRentalRepository.delete(bikeRental);
    }

    @Transactional
    public UnlockResponseDTO unlockBike(Long bikeRentalId, UnlockRequestDTO request) {
        // Fetch bike rental with bike and lock
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // Verify user authorization
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to unlock this bike");
        }

        // Verify bike matches request
        if (!bikeRental.getBike().getId().equals(request.getBikeId())) {
            throw new IllegalArgumentException("Bike ID does not match the rental");
        }

        // Check if bike has a lock
        Lock lock = bikeRental.getBike().getLock();
        if (lock == null) {
            throw new IllegalStateException("This bike does not have a lock assigned");
        }

        // Check if lock has a provider
        if (lock.getLockProvider() == null) {
            throw new IllegalStateException("Lock does not have a provider configured");
        }

        // Generate unlock token
        String unlockToken = lockEncryptionService.generateUnlockToken(bikeRental, lock);

        // Update lock status and last seen
        LockStatus unlockedStatus = lockStatusService.getLockStatusByName("unlocked");
        lock.setLockStatus(unlockedStatus);
        lock.setLastSeenAt(LocalDateTime.now());
        lockRepository.save(lock);

        // Return response
        return UnlockResponseDTO.builder()
                .unlockToken(unlockToken)
                .lockId(lock.getExternalId() != null ? lock.getExternalId() : lock.getId().toString())
                .expiresIn(lockEncryptionService.getTokenExpirationSeconds())
                .algorithm("AES-256")
                .build();
    }

    @Transactional
    public LockResponseDTO lockBike(Long bikeRentalId, LockRequestDTO request) {
        // Fetch bike rental
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // Verify user authorization
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to lock this bike");
        }

        // Verify bike matches request
        if (!bikeRental.getBike().getId().equals(request.getBikeId())) {
            throw new IllegalArgumentException("Bike ID does not match the rental");
        }

        // Verify lock confirmation
        if (!request.getLockConfirmed()) {
            throw new IllegalArgumentException("Lock must be confirmed");
        }

        // Get lock
        Lock lock = bikeRental.getBike().getLock();
        if (lock == null) {
            throw new IllegalStateException("This bike does not have a lock assigned");
        }

        // Update lock status and last seen
        LockStatus lockedStatus = lockStatusService.getLockStatusByName("locked");
        lock.setLockStatus(lockedStatus);
        lock.setLastSeenAt(LocalDateTime.now());
        lockRepository.save(lock);

        // Update bike coordinates if provided
        if (request.getCoordinates() != null) {
            Coordinates coordinates = coordinatesService.createOrUpdateCoordinates(
                    bikeRental.getBike().getCoordinates(),
                    request.getCoordinates()
            );
            bikeRental.getBike().setCoordinates(coordinates);
            bikeRepository.save(bikeRental.getBike());
        }

        // Get rental status (could be "paused" if applicable)
        String rentalStatus = bikeRental.getBikeRentalStatus() != null 
                ? bikeRental.getBikeRentalStatus().getName() 
                : "active";

        return LockResponseDTO.builder()
                .success(true)
                .rentalStatus(rentalStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return bikeRentalRepository.existsByExternalId(externalId);
    }
}
